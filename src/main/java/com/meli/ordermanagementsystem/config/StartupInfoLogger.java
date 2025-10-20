package com.meli.ordermanagementsystem.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Order(2)
public class StartupInfoLogger implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(StartupInfoLogger.class);
    private final Environment environment;

    public StartupInfoLogger(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void run(String... args) throws Exception {
        logStartupBanner();
        logSystemInformation();
        logApplicationConfiguration();
        logDatabaseConfiguration();
        logSecurityConfiguration();
        logAccessUrls();
        logFooter();
    }

    // ... private helper methods from the guide ...
    private void logStartupBanner() {
        logger.info("\n========================================\n  MELI ORDER MANAGEMENT SYSTEM\n========================================");
    }

    private void logSystemInformation() {
        logger.info("\nSYSTEM INFORMATION:\n  - Startup Time: {}\n  - Java Version: {}\n  - Java Vendor: {}\n  - OS Name: {}\n  - OS Version: {}\n  - OS Architecture: {}",
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                System.getProperty("java.version"),
                System.getProperty("java.vendor"),
                System.getProperty("os.name"),
                System.getProperty("os.version"),
                System.getProperty("os.arch"));
    }

    private void logApplicationConfiguration() {
        logger.info("\nAPPLICATION CONFIGURATION:\n  - Application Name: {}\n  - Active Profile: {}\n  - Server Port: {}\n  - Context Path: {}",
                environment.getProperty("spring.application.name"),
                environment.getActiveProfiles().length > 0 ? environment.getActiveProfiles()[0] : "default",
                environment.getProperty("server.port"),
                environment.getProperty("server.servlet.context-path", "/"));
    }

    private void logDatabaseConfiguration() {
        String url = environment.getProperty("spring.datasource.url");
        logger.info("\nDATABASE CONFIGURATION:\n  - Database Type: {}\n  - Database URL: {}\n  - Connection Pool Max: {}\n  - DDL Auto Mode: {}",
                (url != null && url.contains("postgresql")) ? "PostgreSQL" : "Other",
                maskUrl(url),
                environment.getProperty("spring.datasource.hikari.maximum-pool-size"),
                environment.getProperty("spring.jpa.hibernate.ddl-auto"));
    }

    private void logSecurityConfiguration() {
        String includeMessage = environment.getProperty("server.error.include-message");
        String corsEnabled = environment.getProperty("app.cors.enabled");
        logger.info("\nSECURITY CONFIGURATION:\n  - Error Details Exposed: {}\n  - CORS Enabled: {}", "always".equals(includeMessage) ? "YES (Dev Mode)" : "NO", corsEnabled);
        if ("true".equals(corsEnabled)) {
            logger.warn("  - WARNING: CORS is enabled. Ensure this is intentional.");
        }
    }

    private void logAccessUrls() {
        String port = environment.getProperty("server.port");
        String contextPath = environment.getProperty("server.servlet.context-path", "");
        String managementPort = environment.getProperty("management.server.port");
        try {
            String ip = InetAddress.getLocalHost().getHostAddress();
            logger.info("\nACCESS URLS:\n  - Local: http://localhost:{}{}\n  - Network: http://{}:{}{}\n  - API Info: http://localhost:{}{}/api/info\n  - Health: http://localhost:{}{}/api/info/health", port, contextPath, ip, port, contextPath, port, contextPath, port, contextPath);
            if (managementPort != null && !managementPort.equals(port)) {
                logger.info("  - Actuator: http://localhost:{}/actuator", managementPort);
            }
        } catch (Exception e) {
            logger.warn("Could not determine network addresses");
        }
    }

    private void logFooter() {
        logger.info("\n========================================\n  APPLICATION STARTED SUCCESSFULLY\n========================================\n");
    }

    private String maskUrl(String url) {
        if (url == null) return "Not configured";
        if (url.contains("@")) {
            String[] parts = url.split("@");
            if (parts.length > 1) return "jdbc:postgresql://****@" + parts[1];
        }
        return url.replaceAll("\\?.*", "");
    }
}