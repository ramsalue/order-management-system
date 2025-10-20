package com.meli.ordermanagementsystem.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Configuration properties for database connection
 * Provides type-safe access to database configuration
 * Validates properties at startup
 */
@Configuration
@ConfigurationProperties(prefix = "spring.datasource")
@Validated
public class DatabaseProperties {

    @NotBlank(message = "Database URL is required")
    private String url;

    @NotBlank(message = "Database username is required")
    private String username;

    @NotBlank(message = "Database password is required")
    private String password;

    @NotBlank(message = "Database driver class name is required")
    private String driverClassName;

    /**
     * HikariCP connection pool properties
     */
    private HikariProperties hikari = new HikariProperties();

    /**
     * Default constructor
     */
    public DatabaseProperties() {
    }

    // Getters and Setters

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public HikariProperties getHikari() {
        return hikari;
    }

    public void setHikari(HikariProperties hikari) {
        this.hikari = hikari;
    }

    /**
     * Nested class for HikariCP properties
     */
    public static class HikariProperties {

        @NotNull(message = "Connection timeout is required")
        @Min(value = 1000, message = "Connection timeout must be at least 1000ms")
        private Long connectionTimeout = 20000L;

        @NotNull(message = "Maximum pool size is required")
        @Min(value = 1, message = "Maximum pool size must be at least 1")
        @Max(value = 100, message = "Maximum pool size cannot exceed 100")
        private Integer maximumPoolSize = 10;

        @NotNull(message = "Minimum idle connections is required")
        @Min(value = 0, message = "Minimum idle cannot be negative")
        private Integer minimumIdle = 5;

        private Long idleTimeout = 300000L;
        private Long maxLifetime = 1200000L;

        /**
         * Default constructor
         */
        public HikariProperties() {
        }

        // Getters and Setters

        public Long getConnectionTimeout() {
            return connectionTimeout;
        }

        public void setConnectionTimeout(Long connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
        }

        public Integer getMaximumPoolSize() {
            return maximumPoolSize;
        }

        public void setMaximumPoolSize(Integer maximumPoolSize) {
            this.maximumPoolSize = maximumPoolSize;
        }

        public Integer getMinimumIdle() {
            return minimumIdle;
        }

        public void setMinimumIdle(Integer minimumIdle) {
            this.minimumIdle = minimumIdle;
        }

        public Long getIdleTimeout() {
            return idleTimeout;
        }

        public void setIdleTimeout(Long idleTimeout) {
            this.idleTimeout = idleTimeout;
        }

        public Long getMaxLifetime() {
            return maxLifetime;
        }

        public void setMaxLifetime(Long maxLifetime) {
            this.maxLifetime = maxLifetime;
        }
    }
}