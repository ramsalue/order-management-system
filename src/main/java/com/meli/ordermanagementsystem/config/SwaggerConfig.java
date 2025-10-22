package com.meli.ordermanagementsystem.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${app.version:1.0.0}")
    private String appVersion;

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI orderManagementAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(apiServers())
                .tags(apiTags());
    }

    private Info apiInfo() {
        return new Info()
                .title("MELI Order Management System API")
                .description(
                        "REST API for managing clients, items, and orders in the MELI e-commerce platform. " +
                                "This API provides comprehensive CRUD operations with proper validation, " +
                                "error handling, and business logic implementation."
                )
                .version(appVersion)
                .contact(apiContact())
                .license(apiLicense())
                .termsOfService("https://www.meli.com/terms");
    }

    private Contact apiContact() {
        return new Contact()
                .name("Luis Enrique Ramírez Sabino")
                .email("luis.ramirez@meli.com")
                .url("https://github.com/your-username/order-management-system");
    }

    private License apiLicense() {
        return new License()
                .name("Digital NAO - Educational Project")
                .url("https://digitalnao.com");
    }

    private List<Server> apiServers() {
        Server developmentServer = new Server()
                .url("http://localhost:" + serverPort)
                .description("Development Server");

        Server testServer = new Server()
                .url("http://localhost:8081")
                .description("Testing Server");

        Server productionServer = new Server()
                .url("https://api.meli.com")
                .description("Production Server (if deployed)");

        return Arrays.asList(developmentServer, testServer, productionServer);
    }

    private List<Tag> apiTags() {
        Tag clientsTag = new Tag()
                .name("Clients")
                .description("Operations related to client management");

        Tag itemsTag = new Tag()
                .name("Items")
                .description("Operations related to item catalog management");

        Tag ordersTag = new Tag()
                .name("Orders")
                .description("Operations related to order processing and management");

        Tag systemTag = new Tag()
                .name("System")
                .description("System information and health check endpoints");

        return Arrays.asList(clientsTag, itemsTag, ordersTag, systemTag);
    }
}