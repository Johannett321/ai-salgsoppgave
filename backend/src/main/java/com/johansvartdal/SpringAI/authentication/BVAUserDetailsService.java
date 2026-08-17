package com.johansvartdal.SpringAI.authentication;

import com.johansvartdal.SpringAI.model.User;
import com.johansvartdal.SpringAI.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BVAUserDetailsService implements UserDetailsService {

    private final UserService userService;

    public BVAUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Load the user from your database by email
        // Return a UserDetails object
        log.debug("Loading user by email: " + email);
        User user = userService.getUserByEmail(email);
        return new BVAUserDetails(user);
    }
}
