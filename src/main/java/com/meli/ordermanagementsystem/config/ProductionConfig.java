package com.meli.ordermanagementsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("prod")
public class ProductionConfig {

    @Bean
    public org.springframework.boot.CommandLineRunner productionStartupLogger() {
        return args -> {
            System.out.println("========================================");
            System.out.println("PRODUCTION MODE ACTIVATED");
            System.out.println("========================================");
            System.out.println("WARNING: This is a production environment");
            System.out.println("- All debugging features are disabled");
            System.out.println("- Error details are hidden from users");
            System.out.println("- Logging is minimized");
            System.out.println("- Do NOT debug in production");
            System.out.println("========================================");
        };
    }
}