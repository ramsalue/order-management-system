package com.meli.ordermanagementsystem.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base class for integration tests
 * Provides common configuration and utilities for all integration tests
 * Uses test profile with real database and transaction rollback
 */
@SpringBootTest // Loads the full Spring application context
@AutoConfigureMockMvc // Automatically configures MockMvc for simulating HTTP requests
@ActiveProfiles("test") // Ensures the 'test' profile is used for these tests
@Transactional // Rolls back database changes after each test method
public abstract class BaseIntegrationTest {

    @Autowired
    protected MockMvc mockMvc; // Used to perform HTTP requests

    @Autowired
    protected ObjectMapper objectMapper; // Used to convert objects to/from JSON

    /**
     * Converts object to JSON string
     * @param obj object to convert
     * @return JSON string
     * @throws Exception if conversion fails
     */
    protected String asJsonString(final Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    /**
     * Converts JSON string to object
     * @param json JSON string
     * @param clazz target class
     * @return converted object
     * @throws Exception if conversion fails
     */
    protected <T> T fromJsonString(String json, Class<T> clazz) throws Exception {
        return objectMapper.readValue(json, clazz);
    }
}