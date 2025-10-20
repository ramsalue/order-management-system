package com.meli.ordermanagementsystem;

import com.meli.ordermanagementsystem.config.ApplicationProperties;
import com.meli.ordermanagementsystem.config.DatabaseProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.Environment;

@SpringBootApplication
@EnableConfigurationProperties({
        DatabaseProperties.class,
        ApplicationProperties.class
})
public class OrderManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(OrderManagementSystemApplication.class);
        Environment env = app.run(args).getEnvironment();

        // Log profile information
        String profile = env.getActiveProfiles().length > 0
                ? env.getActiveProfiles()[0]
                : "default";
        String port = env.getProperty("server.port");

        System.out.println("\n========================================");
        System.out.println("Application started successfully!");
        System.out.println("Profile: " + profile);
        System.out.println("Port: " + port);
        System.out.println("========================================\n");
    }
}