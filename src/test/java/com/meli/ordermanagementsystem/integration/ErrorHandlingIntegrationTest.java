package com.meli.ordermanagementsystem.integration;

import com.meli.ordermanagementsystem.dto.ClientDTO;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for error handling
 * Verifies that errors are properly handled and returned
 */
public class ErrorHandlingIntegrationTest extends BaseIntegrationTest {

    /**
     * Test 404 error response structure
     */
    @Test
    public void testNotFoundError_Structure() throws Exception {
        mockMvc.perform(get("/api/clients/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    /**
     * Test validation error response structure
     */
    @Test
    public void testValidationError_Structure() throws Exception {
        // Invalid client (empty name, age too young)
        ClientDTO invalidClient = new ClientDTO(null, "", "Address", 15);

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(invalidClient)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.fieldErrors").exists())
                .andExpect(jsonPath("$.fieldErrors.name").exists())
                .andExpect(jsonPath("$.fieldErrors.age").exists());
    }

    /**
     * Test business exception error response
     */
    @Test
    public void testBusinessException_Structure() throws Exception {
        // Try to create duplicate client
        ClientDTO client1 = new ClientDTO(null, "Duplicate Name", "Address 1", 25);

        // Create first client
        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(client1)))
                .andExpect(status().isCreated());

        // Try to create duplicate
        ClientDTO client2 = new ClientDTO(null, "Duplicate Name", "Address 2", 30);

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(client2)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(containsString("already exists")))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    /**
     * Test malformed JSON error
     */
    @Test
    public void testMalformedJson_Error() throws Exception {
        String malformedJson = "{invalid json}";

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test method not allowed error
     */
    @Test
    public void testMethodNotAllowed_Error() throws Exception {
        // Try to PATCH endpoint that doesn't support PATCH
        mockMvc.perform(patch("/api/clients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isMethodNotAllowed());
    }

    /**
     * Test unsupported media type error
     */
    @Test
    public void testUnsupportedMediaType_Error() throws Exception {
        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("plain text"))
                .andExpect(status().isUnsupportedMediaType());
    }
}