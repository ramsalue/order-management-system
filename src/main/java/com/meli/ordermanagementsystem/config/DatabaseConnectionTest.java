package com.meli.ordermanagementsystem.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Database connection test component
 * This class runs at application startup to verify database connectivity
 */
//@Component
public class DatabaseConnectionTest implements CommandLineRunner {

    private final DataSource dataSource;

    /**
     * Constructor with dependency injection
     * @param dataSource the configured data source
     */
    public DatabaseConnectionTest(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Executes at application startup
     * Tests database connection and prints result
     */
    @Override
    public void run(String... args) throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            System.out.println("========================================");
            System.out.println("DATABASE CONNECTION TEST");
            System.out.println("========================================");
            System.out.println("Database: " + connection.getMetaData().getDatabaseProductName());
            System.out.println("Version: " + connection.getMetaData().getDatabaseProductVersion());
            System.out.println("URL: " + connection.getMetaData().getURL());
            System.out.println("Connection Status: SUCCESS");
            System.out.println("========================================");
        } catch (Exception e) {
            System.err.println("========================================");
            System.err.println("DATABASE CONNECTION FAILED");
            System.err.println("Error: " + e.getMessage());
            System.err.println("========================================");
            throw e;
        }
    }
}