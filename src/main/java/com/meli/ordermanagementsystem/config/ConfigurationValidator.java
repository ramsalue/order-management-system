package com.meli.ordermanagementsystem.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Validates application configuration at startup
 * Ensures all required properties are set
 * Provides clear error messages for missing configuration
 */
@Component
public class ConfigurationValidator implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationValidator.class);

    private final Environment environment;
    private final DatabaseProperties databaseProperties;
    private final ApplicationProperties applicationProperties;

    /**
     * Constructor with dependency injection
     * @param environment Spring environment
     * @param databaseProperties database configuration
     * @param applicationProperties application configuration
     */
    public ConfigurationValidator(Environment environment,
                                  DatabaseProperties databaseProperties,
                                  ApplicationProperties applicationProperties) {
        this.environment = environment;
        this.databaseProperties = databaseProperties;
        this.applicationProperties = applicationProperties;
    }

    /**
     * Runs at application startup
     * Validates configuration and logs results
     */
    @Override
    public void run(String... args) throws Exception {
        logger.info("========================================");
        logger.info("CONFIGURATION VALIDATION");
        logger.info("========================================");

        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        // Validate active profile
        validateActiveProfile(errors);

        // Validate database configuration
        validateDatabaseConfiguration(errors, warnings);

        // Validate application configuration
        validateApplicationConfiguration(errors, warnings);

        // Validate environment-specific settings
        validateEnvironmentSettings(warnings);

        // Log results
        logValidationResults(errors, warnings);

        // Fail fast if critical errors found
        if (!errors.isEmpty()) {
            throw new IllegalStateException(
                    "Configuration validation failed"
            );
        }

        logger.info("========================================");
        logger.info("CONFIGURATION VALIDATION PASSED");
        logger.info("========================================");
    }

    /**
     * Validates active profile
     * @param errors list to collect errors
     */
    private void validateActiveProfile(List<String> errors) {
        String[] activeProfiles = environment.getActiveProfiles();

        if (activeProfiles.length == 0) {
            errors.add("No active profile set. Set spring.profiles.active property.");
        } else {
            logger.info("Active Profile: {}", (Object) activeProfiles);

            // Validate profile is one of the expected values
            String profile = activeProfiles[0];
            if (!profile.equals("dev") && !profile.equals("test") && !profile.equals("prod")) {
                errors.add("Unknown profile: " + profile + ". Expected: dev, test, or prod");
            }
        }
    }

    /**
     * Validates database configuration
     * @param errors list to collect errors
     * @param warnings list to collect warnings
     */
    private void validateDatabaseConfiguration(List<String> errors, List<String> warnings) {
        logger.info("Validating database configuration...");

        // Validate URL
        String url = databaseProperties.getUrl();
        if (url == null || url.trim().isEmpty()) {
            errors.add("Database URL is not set");
        } else {
            logger.info("Database URL: {}", maskCredentials(url));

            // Check if using PostgreSQL
            if (!url.contains("postgresql")) {
                warnings.add("Database URL does not contain 'postgresql'. Verify database type.");
            }
        }

        // Validate username
        String username = databaseProperties.getUsername();
        if (username == null || username.trim().isEmpty()) {
            errors.add("Database username is not set");
        } else {
            logger.info("Database Username: {}", username);
        }

        // Validate password
        String password = databaseProperties.getPassword();
        if (password == null || password.trim().isEmpty()) {
            errors.add("Database password is not set");
        } else {
            logger.info("Database Password: ******** (masked)");

            // Warn if using default password in production
            if (isProductionProfile() && password.contains("password")) {
                warnings.add("Production is using a password that contains 'password'. Use a strong password!");
            }
        }

        // Validate connection pool
        validateConnectionPool(warnings);
    }

    /**
     * Validates connection pool configuration
     * @param warnings list to collect warnings
     */
    private void validateConnectionPool(List<String> warnings) {
        DatabaseProperties.HikariProperties hikari = databaseProperties.getHikari();

        logger.info("Connection Pool - Max Size: {}", hikari.getMaximumPoolSize());
        logger.info("Connection Pool - Min Idle: {}", hikari.getMinimumIdle());

        // Validate pool size for production
        if (isProductionProfile()) {
            if (hikari.getMaximumPoolSize() < 10) {
                warnings.add("Production connection pool size is small (" +
                        hikari.getMaximumPoolSize() + "). Consider increasing for better performance.");
            }
        }

        // Validate minimum idle
        if (hikari.getMinimumIdle() > hikari.getMaximumPoolSize()) {
            warnings.add("Minimum idle (" + hikari.getMinimumIdle() +
                    ") is greater than maximum pool size (" + hikari.getMaximumPoolSize() + ")");
        }
    }

    /**
     * Validates application configuration
     * @param errors list to collect errors
     * @param warnings list to collect warnings
     */
    private void validateApplicationConfiguration(List<String> errors, List<String> warnings) {
        logger.info("Validating application configuration...");

        logger.info("Application Name: {}", applicationProperties.getName());
        logger.info("Application Version: {}", applicationProperties.getVersion());

        // Validate server port
        String port = environment.getProperty("server.port");
        if (port != null) {
            logger.info("Server Port: {}", port);
        }
    }

    /**
     * Validates environment-specific settings
     * @param warnings list to collect warnings
     */
    private void validateEnvironmentSettings(List<String> warnings) {
        String activeProfile = environment.getActiveProfiles()[0];

        switch (activeProfile) {
            case "dev":
                validateDevEnvironment(warnings);
                break;
            case "test":
                validateTestEnvironment(warnings);
                break;
            case "prod":
                validateProdEnvironment(warnings);
                break;
        }
    }

    /**
     * Validates development environment settings
     * @param warnings list to collect warnings
     */
    private void validateDevEnvironment(List<String> warnings) {
        String showSql = environment.getProperty("spring.jpa.show-sql");
        if (!"true".equals(showSql)) {
            warnings.add("Development profile: spring.jpa.show-sql is not enabled.");
        }
    }

    /**
     * Validates testing environment settings
     * @param warnings list to collect warnings
     */
    private void validateTestEnvironment(List<String> warnings) {
        String ddlAuto = environment.getProperty("spring.jpa.hibernate.ddl-auto");
        if (!"create-drop".equals(ddlAuto)) {
            warnings.add("Test profile: ddl-auto is not 'create-drop'.");
        }
    }

    /**
     * Validates production environment settings
     * @param warnings list to collect warnings
     */
    private void validateProdEnvironment(List<String> warnings) {
        // Check DDL mode
        String ddlAuto = environment.getProperty("spring.jpa.hibernate.ddl-auto");
        if (!"validate".equals(ddlAuto)) {
            warnings.add("Production profile: ddl-auto is not 'validate'. Database schema may be modified!");
        }

        // Check show SQL
        String showSql = environment.getProperty("spring.jpa.show-sql");
        if ("true".equals(showSql)) {
            warnings.add("Production profile: SQL logging is enabled. This may impact performance.");
        }

        // Check error details
        String includeMessage = environment.getProperty("server.error.include-message");
        if (!"never".equals(includeMessage)) {
            warnings.add("Production profile: Error details are being exposed. Security risk!");
        }

        // Check DevTools
        String devToolsEnabled = environment.getProperty("spring.devtools.restart.enabled");
        if ("true".equals(devToolsEnabled)) {
            warnings.add("Production profile: DevTools is enabled. Disable for production!");
        }
    }

    /**
     * Logs validation results
     * @param errors list of errors
     * @param warnings list of warnings
     */
    private void logValidationResults(List<String> errors, List<String> warnings) {
        if (!errors.isEmpty()) {
            logger.error("========================================");
            logger.error("CONFIGURATION ERRORS FOUND: {}", errors.size());
            logger.error("========================================");
            for (int i = 0; i < errors.size(); i++) {
                logger.error("{}. {}", (i + 1), errors.get(i));
            }
            logger.error("========================================");
        }

        if (!warnings.isEmpty()) {
            logger.warn("========================================");
            logger.warn("CONFIGURATION WARNINGS: {}", warnings.size());
            logger.warn("========================================");
            for (int i = 0; i < warnings.size(); i++) {
                logger.warn("{}. {}", (i + 1), warnings.get(i));
            }
            logger.warn("========================================");
        }

        if (errors.isEmpty() && warnings.isEmpty()) {
            logger.info("No configuration issues found.");
        }
    }

    /**
     * Checks if production profile is active
     * @return true if production profile is active
     */
    private boolean isProductionProfile() {
        String[] profiles = environment.getActiveProfiles();
        return profiles.length > 0 && profiles[0].equals("prod");
    }

    /**
     * Masks credentials in URL for logging
     * @param url database URL
     * @return masked URL
     */
    private String maskCredentials(String url) {
        // Simple masking - replace sensitive parts
        if (url.contains("@")) {
            String[] parts = url.split("@");
            if (parts.length > 1) {
                return "jdbc:postgresql://****@" + parts[1];
            }
        }
        return url;
    }
}