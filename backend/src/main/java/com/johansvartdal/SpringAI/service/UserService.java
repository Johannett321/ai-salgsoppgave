package com.johansvartdal.SpringAI.service;

import com.johansvartdal.SpringAI.authentication.BVAOauth2User;
import com.johansvartdal.SpringAI.authentication.RegistrationDTO;
import com.johansvartdal.SpringAI.authentication.ResetPasswordDTO;
import com.johansvartdal.SpringAI.authentication.UpdatePasswordDTO;
import com.johansvartdal.SpringAI.DTO.UpdateUserDTO;
import com.johansvartdal.SpringAI.exception.*;
import com.johansvartdal.SpringAI.model.*;
import com.johansvartdal.SpringAI.repository.*;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.johansvartdal.SpringAI.utils.EnvironmentUtils.getFrontendUrl;
import static com.johansvartdal.SpringAI.utils.FormatUtils.convertToCamelCase;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final ResetPasswordRepository resetPasswordRepository;
    private final EmailService emailService;
    private final MailChimpService mailChimpService;
    private final PreRegisteredUserRepository preRegisteredUserRepository;

    public UserService(UserRepository userRepository, ResetPasswordRepository resetPasswordRepository, EmailService emailService, MailChimpService mailChimpService, PreRegisteredUserRepository preRegisteredUserRepository) {
        this.userRepository = userRepository;
        this.resetPasswordRepository = resetPasswordRepository;
        this.emailService = emailService;
        this.mailChimpService = mailChimpService;
        this.preRegisteredUserRepository = preRegisteredUserRepository;
    }

    public User getUserByEmail(String email) {
        return userRepository.findUserByEmail(email.toLowerCase()).orElse(null);
    }

    /**
     * Returns the user that is currently logged in
     *
     * @return The user that is currently logged in
     */
    public User getCurrentUser() {
        Authentication token = SecurityContextHolder.getContext().getAuthentication();

        BVAOauth2User principal = null;
        try {
            principal = (BVAOauth2User) token.getPrincipal();
        } catch (Exception ignored) {
        }

        User currentUser;
        if (principal != null) {
            log.trace("Principal: " + principal.getEmail());
            currentUser = getUserByEmail(principal.getEmail());
        } else {
            log.trace("Principal: " + token.getName());
            currentUser = getUserByEmail(token.getName());
        }

        return currentUser;
    }

    /**
     * Creates the user if it does not exist. It is based on the currently logged in user.
     */
    public void createUserIfItDoesNotExist(BVAOauth2User oAuth2User) {
        Optional<User> optionalUser = findUserByOAuthId(oAuth2User);

        if (optionalUser.isPresent()) {
            updateEmailIfNecessary(optionalUser.get(), oAuth2User);
            return;
        }

        // make sure the user has not already registered without oAuth
        ensureEmailNotTaken(oAuth2User.getEmail().toLowerCase());

        // create the user
        createUser(oAuth2User);
    }

    /**
     * Returns the user by searching for it's oAuth Id
     * @param oAuth2User The oAuth2 user to search for
     * @return The user if found. Optional.empty() otherwise.
     */
    private Optional<User> findUserByOAuthId(BVAOauth2User oAuth2User) {
        if (hasFacebookId(oAuth2User)) {
            return userRepository.findByFacebookId(oAuth2User.getFacebookId());
        } else if (hasGoogleSub(oAuth2User)) {
            return userRepository.findByGoogleSub(oAuth2User.getGoogleSub());
        }
        return Optional.empty();
    }


    /**
     * Checks the oAuth2 object if it contains a Facebook ID (like all Facebook signed in users do)
     */
    private boolean hasFacebookId(BVAOauth2User oAuth2User) {
        return oAuth2User.getFacebookId() != null && !oAuth2User.getFacebookId().isEmpty();
    }

    /**
     * Checks the oAuth2 object if it contains a Google SUB (ID) (like all Google signed in users do)
     */
    private boolean hasGoogleSub(BVAOauth2User oAuth2User) {
        return oAuth2User.getGoogleSub() != null && !oAuth2User.getGoogleSub().isEmpty();
    }

    /**
     * Updates the email of a user if it has changed based on the oAuth2 object that the user used to sign in
     */
    private void updateEmailIfNecessary(User user, BVAOauth2User oAuth2User) {
        String newEmail = oAuth2User.getEmail();
        if (newEmail != null && !newEmail.isEmpty() && !user.getEmail().equals(newEmail)) {
            String oldEmail = user.getEmail();
            user.setEmail(newEmail.toLowerCase());
            userRepository.save(user);
            log.info("Email of user {} was changed from {} to {}. Updating database!",
                    oAuth2User.getFirstName(), oldEmail, newEmail);
        }
    }

    /**
     * Throws a conflict exception if the email already exists in the database without oAuth
     */
    private void ensureEmailNotTaken(String email) {
        Optional<User> optionalUserByEmail = userRepository.findUserByEmail(email);
        if (optionalUserByEmail.isPresent()) {
            throw new ConflictException("User with email " + email +
                    " already exists. Please sign in the same way you created your account.");
        }
    }

    /**
     * Creates an AISalgsoppgave user based on an oAuth principal
     * @param oAuth2User The principal to create the user from
     */
    private void createUser(BVAOauth2User oAuth2User) {
        User user = new User(oAuth2User.getEmail().toLowerCase());
        user.setFacebookId(oAuth2User.getFacebookId());
        user.setGoogleSub(oAuth2User.getGoogleSub());
        user.setCreatedAt(LocalDateTime.now());
        user.setFirstName(oAuth2User.getFirstName());
        user.setLastName(oAuth2User.getLastName());
        user = userRepository.save(user);
        log.debug("Created user with ID: {}, as it did not exist", user.getId());
    }

    /**
     * Attempts to register the user
     *
     * @param registrationDTO The DTO containing the registration information
     * @return The user that was created
     */
    @Transactional
    public User attemptRegistration(RegistrationDTO registrationDTO) {
        //make sure the following method throws an error:
        boolean exists = userRepository.existsByEmail(registrationDTO.getEmail());
        if (exists) {
            throw new ConflictException("Email already exists");
        }

        // make sure the password is valid
        if (!passwordIsValid(registrationDTO.getPassword())) {
            throw new BadCredentialsException("Password is not valid");
        }

        // find pre-registered user
        Optional<PreRegisteredUser> preRegisteredUser = preRegisteredUserRepository.findByEmail(registrationDTO.getEmail());

        // capitalize the first letter of each word in the first and last name
        registrationDTO.setFirstName(convertToCamelCase(registrationDTO.getFirstName()));
        registrationDTO.setLastName(convertToCamelCase(registrationDTO.getLastName()));

        // create the user
        User user = new User(registrationDTO.getEmail().toLowerCase());
        user.setFirstName(registrationDTO.getFirstName());
        user.setLastName(registrationDTO.getLastName());
        user.setCreatedAt(LocalDateTime.now());
        user.setPassword(new BCryptPasswordEncoder().encode(registrationDTO.getPassword()));
        user = userRepository.save(user);

        log.info("Created user with email: {}", registrationDTO.getEmail());

        // TODO: Send email to user to verify email address
        try {
            if (preRegisteredUser.isPresent()) {
                mailChimpService.removeTagFromUser(registrationDTO.getEmail(), "pre-registered");
                preRegisteredUserRepository.delete(preRegisteredUser.get());
            }else {
                mailChimpService.addCustomerToMailChimp(user.getEmail(), user.getFirstName(), user.getLastName());
            }
        }catch (Exception e) {
            log.info("Klarte ikke registrere bruker i mailchimp: " + registrationDTO.getEmail());
        }

        return user;
    }

    public boolean amIAuthenticated() {
        // check if authenticated. If not, throw 401
        Authentication token = SecurityContextHolder.getContext().getAuthentication();
        if (token == null) {
            log.trace("Token is null");
            throw new UnauthorizedException();
        }

        if (!token.isAuthenticated()) {
            log.trace("Token not authenticated");
            throw new UnauthorizedException();
        }

        if ((SecurityContextHolder.getContext().getAuthentication()
                instanceof AnonymousAuthenticationToken)) {
            log.trace("Anonymous token!");
            throw new UnauthorizedException();
        }

        return true;
    }

    /**
     * Sends an email to the user with a link to reset the password
     *
     * @param resetPasswordDTO The DTO containing the email of the user
     */
    public void resetPassword(ResetPasswordDTO resetPasswordDTO) {
        User user = getUserByEmail(resetPasswordDTO.getEmail());
        ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest();
        resetPasswordRequest.setEmail(user.getEmail().toLowerCase());
        resetPasswordRequest.setExpiration(System.currentTimeMillis() + 1000 * 60 * 20);
        resetPasswordRepository.save(resetPasswordRequest);

        Email email = new Email();
        email.setTo(user.getEmail());
        email.setSubject("Nullstill passord");
        email.setBody("Klikk her for å nullstille passordet ditt: " + getFrontendUrl() + "change-password?token=" + resetPasswordRequest.getId());
        try {
            emailService.sendEmail(email);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Updates the password for the user
     *
     * @param updatePasswordDTO The DTO containing the new password and the token
     */
    public void updatePassword(UpdatePasswordDTO updatePasswordDTO) {
        // check if token exists
        ResetPasswordRequest request = resetPasswordRepository.findById(updatePasswordDTO.getToken()).orElseThrow(NotFoundException::new);

        // check if token is expired
        if (System.currentTimeMillis() > request.getExpiration()) {
            throw new NotFoundException();
        }

        // make sure new password is valid
        if (!passwordIsValid(updatePasswordDTO.getPassword())) {
            throw new BadCredentialsException("Password is not valid");
        }

        // update password
        User user = getUserByEmail(request.getEmail());
        user.setPassword(new BCryptPasswordEncoder().encode(updatePasswordDTO.getPassword()));
        userRepository.save(user);

        // delete token
        resetPasswordRepository.delete(request);
    }

    /**
     * Updates the user
     *
     * @param userUpdateDTO The DTO containing the new information for the user
     */
    public void updateUser(UpdateUserDTO userUpdateDTO) {
        User user = getCurrentUser();
        user.setEmail(userUpdateDTO.getEmail().toLowerCase());
        user.setFirstName(userUpdateDTO.getFirstName());
        user.setLastName(userUpdateDTO.getLastName());

        if (userUpdateDTO.getNewPassword() != null || userUpdateDTO.getOldPassword() != null) {
            if (!new BCryptPasswordEncoder().matches(userUpdateDTO.getOldPassword(), user.getPassword())) {
                throw new BadCredentialsException("Old password is not correct");
            }
            if (!passwordIsValid(userUpdateDTO.getNewPassword())) {
                throw new BadRequestException("New password is not valid");
            }
            user.setPassword(new BCryptPasswordEncoder().encode(userUpdateDTO.getNewPassword()));
        }
        userRepository.save(user);
    }

    /**
     * Checks if the password is valid. NB: IT DOES NOT CHECK IF THE PASSWORD IS CORRECT FOR THE USER. ONLY IF IT IS VALID AND FOLLOWS THE RULES.
     *
     * @param password The password to check
     * @return True if the password is valid, false otherwise
     */
    public Boolean passwordIsValid(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        return password.length() >= 8 && password.length() <= 128 && password.matches(".*[a-z].*") && password.matches(".*[A-Z].*") && password.matches(".*[0-9].*");
    }

    public void updateUserLastSignIn() {
        User currentUser = getCurrentUser();
        currentUser.setLastLogin(LocalDateTime.now());
        userRepository.save(currentUser);
    }

}
