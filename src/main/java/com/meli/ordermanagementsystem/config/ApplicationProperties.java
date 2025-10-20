package com.meli.ordermanagementsystem.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Configuration properties for application settings
 * Provides type-safe access to application configuration
 */
@Configuration
@ConfigurationProperties(prefix = "app")
@Validated
public class ApplicationProperties {

    @NotBlank(message = "Application name is required")
    private String name = "MELI Order Management System";

    @NotBlank(message = "Application version is required")
    private String version = "1.0.0";

    private String environment;

    private CorsProperties cors = new CorsProperties();

    /**
     * Default constructor
     */
    public ApplicationProperties() {
    }

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public CorsProperties getCors() {
        return cors;
    }

    public void setCors(CorsProperties cors) {
        this.cors = cors;
    }

    /**
     * Nested class for CORS properties
     */
    public static class CorsProperties {

        private boolean enabled = false;
        private String[] allowedOrigins = new String[]{};
        private String[] allowedMethods = new String[]{"GET", "POST", "PUT", "DELETE", "PATCH"};
        private String[] allowedHeaders = new String[]{"*"};
        private boolean allowCredentials = false;

        /**
         * Default constructor
         */
        public CorsProperties() {
        }

        // Getters and Setters

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String[] getAllowedOrigins() {
            return allowedOrigins;
        }

        public void setAllowedOrigins(String[] allowedOrigins) {
            this.allowedOrigins = allowedOrigins;
        }

        public String[] getAllowedMethods() {
            return allowedMethods;
        }

        public void setAllowedMethods(String[] allowedMethods) {
            this.allowedMethods = allowedMethods;
        }

        public String[] getAllowedHeaders() {
            return allowedHeaders;
        }

        public void setAllowedHeaders(String[] allowedHeaders) {
            this.allowedHeaders = allowedHeaders;
        }

        public boolean isAllowCredentials() {
            return allowCredentials;
        }

        public void setAllowCredentials(boolean allowCredentials) {
            this.allowCredentials = allowCredentials;
        }
    }
}