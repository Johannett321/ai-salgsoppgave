package com.johansvartdal.SpringAI.utils;

import com.johansvartdal.SpringAI.enums.Environment;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EnvironmentUtils {

    /**
     * Returns the URL to the frontend
     * @return The URL to the frontend
     */
    public static String getFrontendUrl() {
        switch (getEnvironment()) {
            case PRODUCTION -> { return "https://app.aisalgsoppgave.no/"; } // TODO: Add url to production environment
            case STAGING -> { return "http://localhost:3000/"; }
            case DEVELOPMENT -> { return "http://localhost:3000/"; }
            default -> { throw new RuntimeException("Unknown environment") ; }
        }
    }

    public static String getBackendUrl() {
        switch (getEnvironment()) {
            case PRODUCTION -> { return "https://api.aisalgsoppgave.no/"; } // TODO: Add url to production environment
            case STAGING -> { return "http://localhost:8080/"; }
            case DEVELOPMENT -> { return "http://localhost:8080/"; }
            default -> { throw new RuntimeException("Unknown environment") ; }
        }
    }

    /**
     * Returns the environment that the application is running in
     * @return The environment that the application is running in
     */
    public static Environment getEnvironment() {
        String environment = System.getenv("ENVIRONMENT");
        if (environment == null) {
            log.warn("Warning: The 'ENVIRONMENT' environment variable is not set. Using DEV as default. Valid values: [PRODUCTION, STAGING, DEV]");
            return Environment.DEVELOPMENT;
        }

        switch (environment.toUpperCase()) {
            case "PRODUCTION":
                return Environment.PRODUCTION;
            case "STAGING":
                return Environment.STAGING;
            case "DEV":
                return Environment.DEVELOPMENT;
            default:
                throw new RuntimeException("Unknown environment: " + environment);
        }
    }
}
