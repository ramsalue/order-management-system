package com.meli.ordermanagementsystem.integration;

import com.meli.ordermanagementsystem.dto.ClientDTO;
import com.meli.ordermanagementsystem.model.Client;
import com.meli.ordermanagementsystem.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Client API endpoints
 * Tests complete request/response cycle with database
 */
public class ClientControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ClientRepository clientRepository;

    @BeforeEach
    public void setUp() {
        // Clean up before each test
        clientRepository.deleteAll();
    }

    /**
     * Test creating a client through API
     */
    @Test
    public void testCreateClient_Success() throws Exception {
        // Given
        ClientDTO clientDTO = new ClientDTO(null, "John Doe", "123 Main St", 30);

        // When & Then
        MvcResult result = mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(clientDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idClient").exists())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.address").value("123 Main St"))
                .andExpect(jsonPath("$.age").value(30))
                .andReturn();

        // Verify in database
        String responseJson = result.getResponse().getContentAsString();
        ClientDTO createdClient = fromJsonString(responseJson, ClientDTO.class);

        Client clientInDb = clientRepository.findById(createdClient.getIdClient()).orElse(null);
        assertThat(clientInDb).isNotNull();
        assertThat(clientInDb.getName()).isEqualTo("John Doe");
    }

    /**
     * Test creating client with invalid data (validation failure)
     */
    @Test
    public void testCreateClient_ValidationFailure_AgeTooYoung() throws Exception {
        // Given - age is 17 (below minimum 18)
        ClientDTO clientDTO = new ClientDTO(null, "Young Client", "456 Oak Ave", 17);

        // When & Then
        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(clientDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors.age").exists());
    }

    /**
     * Test creating client with missing required field
     */
    @Test
    public void testCreateClient_ValidationFailure_MissingName() throws Exception {
        // Given - name is empty
        ClientDTO clientDTO = new ClientDTO(null, "", "789 Pine St", 25);

        // When & Then
        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(clientDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.name").exists());
    }

    /**
     * Test creating duplicate client (business rule violation)
     */
    @Test
    public void testCreateClient_DuplicateName() throws Exception {
        // Given - create first client
        Client existingClient = new Client("Duplicate Name", "111 First St", 28);
        clientRepository.save(existingClient);

        // When - try to create client with same name
        ClientDTO clientDTO = new ClientDTO(null, "Duplicate Name", "222 Second St", 30);

        // Then
        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(clientDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("already exists")));
    }

    /**
     * Test getting client by ID
     */
    @Test
    public void testGetClientById_Success() throws Exception {
        // Given
        Client client = new Client("Jane Smith", "321 Elm St", 35);
        Client savedClient = clientRepository.save(client);

        // When & Then
        mockMvc.perform(get("/api/clients/" + savedClient.getIdClient()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idClient").value(savedClient.getIdClient()))
                .andExpect(jsonPath("$.name").value("Jane Smith"))
                .andExpect(jsonPath("$.age").value(35));
    }

    /**
     * Test getting non-existent client (404)
     */
    @Test
    public void testGetClientById_NotFound() throws Exception {
        // Given - non-existent ID
        Long nonExistentId = 99999L;

        // When & Then
        mockMvc.perform(get("/api/clients/" + nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(containsString("not found")));
    }

    /**
     * Test getting all clients
     */
    @Test
    public void testGetAllClients_Success() throws Exception {
        // Given
        clientRepository.save(new Client("Client 1", "Address 1", 25));
        clientRepository.save(new Client("Client 2", "Address 2", 30));
        clientRepository.save(new Client("Client 3", "Address 3", 35));

        // When & Then
        mockMvc.perform(get("/api/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[1].name").exists())
                .andExpect(jsonPath("$[2].name").exists());
    }

    /**
     * Test getting all clients when empty
     */
    @Test
    public void testGetAllClients_Empty() throws Exception {
        // Given - no clients in database

        // When & Then
        mockMvc.perform(get("/api/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    /**
     * Test updating client
     */
    @Test
    public void testUpdateClient_Success() throws Exception {
        // Given
        Client client = new Client("Old Name", "Old Address", 25);
        Client savedClient = clientRepository.save(client);

        ClientDTO updateDTO = new ClientDTO(
                savedClient.getIdClient(),
                "New Name",
                "New Address",
                26
        );

        // When & Then
        mockMvc.perform(put("/api/clients/" + savedClient.getIdClient())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"))
                .andExpect(jsonPath("$.address").value("New Address"))
                .andExpect(jsonPath("$.age").value(26));

        // Verify in database
        Client updatedClient = clientRepository.findById(savedClient.getIdClient()).orElse(null);
        assertThat(updatedClient).isNotNull();
        assertThat(updatedClient.getName()).isEqualTo("New Name");
    }

    /**
     * Test updating non-existent client
     */
    @Test
    public void testUpdateClient_NotFound() throws Exception {
        // Given
        Long nonExistentId = 99999L;
        ClientDTO updateDTO = new ClientDTO(nonExistentId, "New Name", "New Address", 30);

        // When & Then
        mockMvc.perform(put("/api/clients/" + nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updateDTO)))
                .andExpect(status().isNotFound());
    }

    /**
     * Test deleting client
     */
    @Test
    public void testDeleteClient_Success() throws Exception {
        // Given
        Client client = new Client("Delete Me", "Delete Address", 28);
        Client savedClient = clientRepository.save(client);

        // When & Then
        mockMvc.perform(delete("/api/clients/" + savedClient.getIdClient()))
                .andExpect(status().isNoContent());

        // Verify deleted from database
        assertThat(clientRepository.findById(savedClient.getIdClient())).isEmpty();
    }

    /**
     * Test deleting non-existent client
     */
    @Test
    public void testDeleteClient_NotFound() throws Exception {
        // Given
        Long nonExistentId = 99999L;

        // When & Then
        mockMvc.perform(delete("/api/clients/" + nonExistentId))
                .andExpect(status().isNotFound());
    }

    /**
     * Test searching clients by name
     */
    @Test
    public void testSearchClientsByName_Success() throws Exception {
        // Given
        clientRepository.save(new Client("John Smith", "Address 1", 25));
        clientRepository.save(new Client("John Doe", "Address 2", 30));
        clientRepository.save(new Client("Jane Doe", "Address 3", 35));

        // When & Then - search for "John"
        mockMvc.perform(get("/api/clients/search")
                        .param("name", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", hasItems(
                        containsString("John"),
                        containsString("John")
                )));
    }

    /**
     * Test getting clients by age range
     */
    @Test
    public void testGetClientsByAgeRange_Success() throws Exception {
        // Given
        clientRepository.save(new Client("Young Client", "Address 1", 20));
        clientRepository.save(new Client("Middle Client", "Address 2", 30));
        clientRepository.save(new Client("Old Client", "Address 3", 40));

        // When & Then - search for ages 25-35
        mockMvc.perform(get("/api/clients/age-range")
                        .param("minAge", "25")
                        .param("maxAge", "35"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Middle Client"))
                .andExpect(jsonPath("$[0].age").value(30));
    }

    /**
     * Test complete CRUD workflow
     */
    @Test
    public void testCompleteCRUDWorkflow() throws Exception {
        // 1. Create
        ClientDTO createDTO = new ClientDTO(null, "Workflow Client", "Workflow Address", 28);
        MvcResult createResult = mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(createDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        ClientDTO createdClient = fromJsonString(
                createResult.getResponse().getContentAsString(),
                ClientDTO.class
        );
        Long clientId = createdClient.getIdClient();

        // 2. Read
        mockMvc.perform(get("/api/clients/" + clientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Workflow Client"));

        // 3. Update
        ClientDTO updateDTO = new ClientDTO(
                clientId,
                "Updated Workflow Client",
                "Updated Address",
                29
        );
        mockMvc.perform(put("/api/clients/" + clientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Workflow Client"));

        // 4. Delete
        mockMvc.perform(delete("/api/clients/" + clientId))
                .andExpect(status().isNoContent());

        // 5. Verify deleted
        mockMvc.perform(get("/api/clients/" + clientId))
                .andExpect(status().isNotFound());
    }
}