package com.johansvartdal.SpringAI.controller;

import com.johansvartdal.SpringAI.DTO.UpdateUserDTO;
import com.johansvartdal.SpringAI.annotation.NoLogin;
import com.johansvartdal.SpringAI.authentication.RegistrationDTO;
import com.johansvartdal.SpringAI.authentication.ResetPasswordDTO;
import com.johansvartdal.SpringAI.authentication.UpdatePasswordDTO;
import com.johansvartdal.SpringAI.model.User;
import com.johansvartdal.SpringAI.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    public UserController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping(path = "/me")
    public ResponseEntity<?> getMe() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @PutMapping()
    public ResponseEntity<?> putUser(@RequestBody @Validated UpdateUserDTO updateUserDTO) {
        userService.updateUser(updateUserDTO);
        return ResponseEntity.ok().build();
    }

    @NoLogin
    @GetMapping(path="/authenticated")
    public ResponseEntity<?> amIAuthenticated() {
        return ResponseEntity.ok(userService.amIAuthenticated());
    }

    @NoLogin
    @PostMapping(path = "/register")
    public ResponseEntity<?> register(@RequestBody RegistrationDTO registrationDTO, HttpServletRequest request) {
        User registeredUser = userService.attemptRegistration(registrationDTO);

        // Manually authenticate the user after successful registration
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                registeredUser.getEmail(),
                registrationDTO.getPassword()
        );

        // Use the authentication manager to authenticate the user
        Authentication auth = authenticationManager.authenticate(authentication);

        // Set the authentication to the security context
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Create a new session for the user
        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

        userService.updateUserLastSignIn();
        return ResponseEntity.ok(registeredUser);
    }

    @NoLogin
    @PostMapping(path = "/forgot-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) {
        userService.resetPassword(resetPasswordDTO);
        return ResponseEntity.ok("Password reset successfully");
    }

    @NoLogin
    @PutMapping(path = "/reset-password")
    public ResponseEntity<?> updatePassword(@RequestBody UpdatePasswordDTO updatePasswordDTO) {
        userService.updatePassword(updatePasswordDTO);
        return ResponseEntity.ok("Password updated successfully");
    }

    @PutMapping(path = "/update")
    public ResponseEntity<?> updateUser(@Validated @RequestBody UpdateUserDTO userUpdateDTO) {
        userService.updateUser(userUpdateDTO);
        return ResponseEntity.ok("User updated successfully");
    }
}
