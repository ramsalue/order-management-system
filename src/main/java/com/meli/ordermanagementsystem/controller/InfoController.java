package com.meli.ordermanagementsystem.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/info")
public class InfoController {

    private final Environment environment;

    // Use default value if property not found
    @Value("${spring.application.name:order-management-system}")
    private String applicationName;

    @Value("${server.port:8080}")
    private String serverPort;

    public InfoController(Environment environment) {
        this.environment = environment;
    }

    @GetMapping
    public Map<String, Object> getInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("application", applicationName);
        info.put("activeProfiles", Arrays.toString(environment.getActiveProfiles()));
        info.put("defaultProfiles", Arrays.toString(environment.getDefaultProfiles()));
        info.put("serverPort", serverPort);
        info.put("timestamp", System.currentTimeMillis());
        return info;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("profile", environment.getActiveProfiles().length > 0
                ? environment.getActiveProfiles()[0]
                : "default");
        return health;
    }

    /**
     * Gets detailed environment information
     * GET /api/info/environment
     * @return Map with environment details
     */
    @GetMapping("/environment")
    public Map<String, Object> getEnvironmentInfo() {
        Map<String, Object> envInfo = new HashMap<>();

        // Active profile
        String[] activeProfiles = environment.getActiveProfiles();
        envInfo.put("activeProfile", activeProfiles.length > 0 ? activeProfiles[0] : "none");

        // Database info (masked)
        String dbUrl = environment.getProperty("spring.datasource.url");
        envInfo.put("databaseType", dbUrl != null && dbUrl.contains("postgresql") ? "PostgreSQL" : "Unknown");
        envInfo.put("databaseHost", maskDatabaseUrl(dbUrl));

        // Connection pool
        envInfo.put("connectionPoolMax", environment.getProperty("spring.datasource.hikari.maximum-pool-size"));
        envInfo.put("connectionPoolMin", environment.getProperty("spring.datasource.hikari.minimum-idle"));

        // JPA settings
        envInfo.put("ddlAuto", environment.getProperty("spring.jpa.hibernate.ddl-auto"));
        envInfo.put("showSql", environment.getProperty("spring.jpa.show-sql"));

        // Application settings
        envInfo.put("applicationName", environment.getProperty("spring.application.name"));
        envInfo.put("serverPort", environment.getProperty("server.port"));

        // Java environment
        envInfo.put("javaVersion", System.getProperty("java.version"));
        envInfo.put("javaVendor", System.getProperty("java.vendor"));

        return envInfo;
    }

    /**
     * Masks sensitive information in database URL
     * @param url database URL
     * @return masked URL
     */
    private String maskDatabaseUrl(String url) {
        if (url == null) {
            return "Not configured";
        }

        // Extract just the host and database name
        try {
            if (url.contains("//") && url.contains("/")) {
                String hostPart = url.substring(url.indexOf("//") + 2);
                if (hostPart.contains("/")) {
                    return hostPart.substring(0, hostPart.indexOf("/"));
                }
            }
        } catch (Exception e) {
            return "****";
        }

        return "****";
    }
}