package com.johansvartdal.SpringAI.authentication;

import com.johansvartdal.SpringAI.enums.Environment;
import com.johansvartdal.SpringAI.service.OAuth2Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static com.johansvartdal.SpringAI.utils.EnvironmentUtils.getEnvironment;
import static com.johansvartdal.SpringAI.utils.EnvironmentUtils.getFrontendUrl;

@Slf4j
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    private OAuth2Service oAuth2Service;

    @Autowired
    private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Autowired
    private BVAUserDetailsService userDetailsService;

    @Autowired
    private NoSecurityProcessor noSecurityProcessor;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, LoginSuccessHandler loginSuccessHandler) throws Exception {
        //TODO: Setup proper CSRF

        // findNoSecurityPaths();
        noSecurityProcessor.findNoSecurityPaths();

        // auth.requestMatchers("/license/check").permitAll();
        return http
                .csrf(csrf -> csrf.disable())
                .cors(httpSecurityCorsConfigurer -> {
                    httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource());
                })
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/").permitAll(); // anyone can access
                    auth.requestMatchers("/error").permitAll();
                    auth.requestMatchers("/actuator/*").permitAll();
                    noSecurityProcessor.getNoLoginPaths().forEach(path ->{
                        auth.requestMatchers(path).permitAll();
                    });

                    auth.anyRequest().authenticated();  // all other pages needs login
                })
                .formLogin((form) -> form
                        .loginPage("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler(loginSuccessHandler)
                        .failureHandler((request, response, exception) -> {
                            response.sendRedirect(getFrontendUrl() + "login?error=Invalid%20credentials");
                        })
                        .permitAll()
                )
                .authenticationProvider(daoAuthenticationProvider())
                .oauth2Login(oAuth2 -> {
                    oAuth2.loginPage(getFrontendUrl() + "/login");
                    oAuth2.userInfoEndpoint(userInfo -> {
                        userInfo.userService(oAuth2Service);
                    });

                    oAuth2.successHandler(oAuth2LoginSuccessHandler);
                })
                .logout(LogoutConfigurer::permitAll)
                .sessionManagement(session -> { session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .invalidSessionUrl("/invalidSession")
                        .sessionFixation().migrateSession();
                })
                .build();
    }

    @Bean
    public AuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(new BCryptPasswordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(new BCryptPasswordEncoder())
                .and()
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configurationForNoSecurity = new CorsConfiguration();

        configurationForNoSecurity.setAllowedOrigins(Collections.singletonList("*")); // Allow all origins
        configurationForNoSecurity.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configurationForNoSecurity.setAllowedHeaders(Arrays.asList("*")); // Allow all headers
        configurationForNoSecurity.setExposedHeaders(Arrays.asList("x-auth-token"));

        CorsConfiguration configurationForBVA = new CorsConfiguration();

        ArrayList<String> allowedOrigins = new ArrayList<>();
        allowedOrigins.add(getFrontendUrl());
        allowedOrigins.add("https://app.aisalgsoppgave.no");
        allowedOrigins.add("https://www.aisalgsoppgave.no");
        allowedOrigins.add("https://aisalgsoppgave.no");

        if (getEnvironment() == Environment.DEVELOPMENT) {
            allowedOrigins.addAll(Arrays.asList("http://127.0.0.1:9292"));
        }

        configurationForBVA.setAllowedOrigins(allowedOrigins); // Allow all origins
        configurationForBVA.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configurationForBVA.setAllowedHeaders(Arrays.asList("*")); // Allow all headers
        configurationForBVA.setExposedHeaders(Arrays.asList("x-auth-token"));
        configurationForBVA.setAllowCredentials(true); // Important for cookies

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        noSecurityProcessor.getAllowAllOriginsPaths().forEach(path -> {
            log.info("Allowing * requests for path {}", path);
            source.registerCorsConfiguration(path, configurationForNoSecurity);
        });
        source.registerCorsConfiguration("/**", configurationForBVA);

        return source;
    }
}
