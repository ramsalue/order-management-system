package com.meli.ordermanagementsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
@Profile("dev")
public class DevelopmentConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:4200"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Bean
    public org.springframework.boot.CommandLineRunner developmentStartupLogger() {
        return args -> {
            System.out.println("========================================");
            System.out.println("DEVELOPMENT MODE ACTIVATED");
            System.out.println("========================================");
            System.out.println("- CORS enabled for local development");
            System.out.println("- DevTools active reload enabled");
            System.out.println("- Detailed logging enabled");
            System.out.println("- SQL queries will be logged");
            System.out.println("========================================");
        };
    }
}