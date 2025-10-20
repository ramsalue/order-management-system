package com.meli.ordermanagementsystem.service;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class EnvironmentService {

    private final Environment environment;

    public EnvironmentService(Environment environment) {
        this.environment = environment;
    }

    public String getActiveProfile() {
        String[] profiles = environment.getActiveProfiles();
        return profiles.length > 0 ? profiles[0] : "default";
    }

    public boolean isDevelopment() {
        return Arrays.asList(environment.getActiveProfiles()).contains("dev");
    }

    // ... other methods from the guide ...
    public boolean isTesting() {
        return Arrays.asList(environment.getActiveProfiles()).contains("test");
    }

    public boolean isProduction() {
        return Arrays.asList(environment.getActiveProfiles()).contains("prod");
    }

    public Map<String, Object> getEnvironmentSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("activeProfile", getActiveProfile());
        summary.put("isDevelopment", isDevelopment());
        summary.put("isTesting", isTesting());
        summary.put("isProduction", isProduction());
        summary.put("applicationName", environment.getProperty("spring.application.name"));
        summary.put("serverPort", environment.getProperty("server.port"));
        summary.put("databaseUrl", maskSensitiveData(environment.getProperty("spring.datasource.url")));
        summary.put("javaVersion", System.getProperty("java.version"));
        return summary;
    }

    private String maskSensitiveData(String data) {
        if (data == null) return "Not configured";
        if (data.length() < 10) return "****";
        return data.substring(0, 10) + "****";
    }
}