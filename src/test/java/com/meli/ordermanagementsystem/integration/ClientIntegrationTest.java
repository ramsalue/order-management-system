package com.meli.ordermanagementsystem.integration;

import com.meli.ordermanagementsystem.model.Client;
import com.meli.ordermanagementsystem.repository.ClientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for the Client entity and repository.
 * @SpringBootTest: This tells JUnit to load the full Spring application context for this test.
 * @ActiveProfiles("test"): This explicitly forces the "test" profile to be active.
 * @Transactional: For tests, this annotation automatically rolls back the transaction
 * after each test method, ensuring tests are isolated and don't affect each other.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ClientIntegrationTest {

    @Autowired // Spring automatically injects an instance of our ClientRepository.
    private ClientRepository clientRepository;

    @Test // Marks this method as a JUnit test case.
    public void testCreateAndFindClient() {
        // Given: We create a new client object.
        Client client = new Client("Test Client", "123 Test Street", 25);

        // When: We save it to the database and then try to find it.
        Client savedClient = clientRepository.save(client);
        Client foundClient = clientRepository.findById(savedClient.getIdClient()).orElse(null);

        // Then: We assert that the client was found and its data is correct.
        assertThat(foundClient).isNotNull();
        assertThat(foundClient.getName()).isEqualTo("Test Client");
    }
}