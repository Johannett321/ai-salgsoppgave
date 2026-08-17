package com.johansvartdal.SpringAI.authentication;

import com.johansvartdal.SpringAI.exception.ConflictException;
import com.johansvartdal.SpringAI.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.johansvartdal.SpringAI.utils.EnvironmentUtils.getFrontendUrl;

@Component
@Slf4j
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final UserService userService;

    public OAuth2LoginSuccessHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        BVAOauth2User oAuth2User = (BVAOauth2User) authentication.getPrincipal();

        try {
            userService.createUserIfItDoesNotExist(oAuth2User);
        }catch (ConflictException conflictException) {
            authentication.setAuthenticated(false);
            response.sendRedirect(getFrontendUrl() + "/login?error=" + conflictException.getMessage());
            return;
        }


        log.debug("User logged in: {} ({})", oAuth2User.getFirstName(), oAuth2User.getEmail());
        userService.updateUserLastSignIn();

        response.sendRedirect(getFrontendUrl());
        super.onAuthenticationSuccess(request, response, authentication);
    }
}