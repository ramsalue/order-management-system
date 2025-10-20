package com.meli.ordermanagementsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class TestConfig {

    @Bean
    public org.springframework.boot.CommandLineRunner testStartupLogger() {
        return args -> {
            System.out.println("========================================");
            System.out.println("TESTING MODE ACTIVATED");
            System.out.println("========================================");
            System.out.println("- Test database active");
            System.out.println("- Transactions will rollback");
            System.out.println("- Schema will be dropped after tests");
            System.out.println("========================================");
        };
    }
}