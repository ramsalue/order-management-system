package com.meli.ordermanagementsystem.service;

import com.meli.ordermanagementsystem.exception.BusinessException;
import com.meli.ordermanagementsystem.exception.ResourceNotFoundException;
import com.meli.ordermanagementsystem.model.Client;
import com.meli.ordermanagementsystem.repository.ClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for Client business logic
 * Handles all client-related operations and validations
 */
@Service
@Transactional
public class ClientService {

    private final ClientRepository clientRepository;

    /**
     * Constructor with dependency injection
     * @param clientRepository the client repository
     */
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    /**
     * Creates a new client
     * Validates that client name is unique
     * @param client the client to create
     * @return the created client with generated ID
     * @throws BusinessException if client name already exists
     */
    public Client createClient(Client client) {
        // Validate unique name
        if (clientRepository.existsByName(client.getName())) {
            throw new BusinessException("Client with name '" + client.getName() + "' already exists");
        }

        // Additional business validations
        validateClientAge(client.getAge());

        return clientRepository.save(client);
    }

    /**
     * Retrieves a client by ID
     * @param id the client ID
     * @return the client
     * @throws ResourceNotFoundException if client not found
     */
    @Transactional(readOnly = true)
    public Client getClientById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", id));
    }

    /**
     * Retrieves all clients
     * @return list of all clients
     */
    @Transactional(readOnly = true)
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    /**
     * Searches clients by name (partial match)
     * @param name partial name to search
     * @return list of matching clients
     */
    @Transactional(readOnly = true)
    public List<Client> searchClientsByName(String name) {
        return clientRepository.findByNameContainingIgnoreCase(name);
    }

    /**
     * Finds clients within an age range
     * @param minAge minimum age
     * @param maxAge maximum age
     * @return list of clients in age range
     */
    @Transactional(readOnly = true)
    public List<Client> getClientsByAgeRange(Integer minAge, Integer maxAge) {
        validateAgeRange(minAge, maxAge);
        return clientRepository.findByAgeBetween(minAge, maxAge);
    }

    /**
     * Updates an existing client
     * @param id the client ID
     * @param clientDetails the updated client data
     * @return the updated client
     * @throws ResourceNotFoundException if client not found
     */
    public Client updateClient(Long id, Client clientDetails) {
        Client client = getClientById(id);

        // Check if name is being changed and if new name already exists
        if (!client.getName().equals(clientDetails.getName()) &&
                clientRepository.existsByName(clientDetails.getName())) {
            throw new BusinessException("Client with name '" + clientDetails.getName() + "' already exists");
        }

        // Validate age
        validateClientAge(clientDetails.getAge());

        // Update fields
        client.setName(clientDetails.getName());
        client.setAddress(clientDetails.getAddress());
        client.setAge(clientDetails.getAge());

        return clientRepository.save(client);
    }

    /**
     * Deletes a client by ID
     * Validates that client has no orders before deletion
     * @param id the client ID
     * @throws ResourceNotFoundException if client not found
     * @throws BusinessException if client has orders
     */
    public void deleteClient(Long id) {
        Client client = getClientById(id);

        // Business rule: cannot delete client with orders
        Long orderCount = clientRepository.countOrdersByClientId(id);
        if (orderCount > 0) {
            throw new BusinessException("Cannot delete client with existing orders. Client has " + orderCount + " orders.");
        }

        clientRepository.delete(client);
    }

    /**
     * Gets clients who have placed orders
     * @return list of clients with orders
     */
    @Transactional(readOnly = true)
    public List<Client> getClientsWithOrders() {
        return clientRepository.findClientsWithOrders();
    }

    /**
     * Gets clients who have never placed orders
     * @return list of clients without orders
     */
    @Transactional(readOnly = true)
    public List<Client> getClientsWithoutOrders() {
        return clientRepository.findClientsWithoutOrders();
    }

    /**
     * Gets order count for a specific client
     * @param id the client ID
     * @return number of orders
     */
    @Transactional(readOnly = true)
    public Long getClientOrderCount(Long id) {
        getClientById(id); // Verify client exists
        return clientRepository.countOrdersByClientId(id);
    }

    /**
     * Private helper method to validate client age
     * @param age the age to validate
     * @throws BusinessException if age is invalid
     */
    private void validateClientAge(Integer age) {
        if (age < 18) {
            throw new BusinessException("Client must be at least 18 years old");
        }
        if (age > 120) {
            throw new BusinessException("Invalid age: " + age);
        }
    }

    /**
     * Private helper method to validate age range
     * @param minAge minimum age
     * @param maxAge maximum age
     * @throws BusinessException if range is invalid
     */
    private void validateAgeRange(Integer minAge, Integer maxAge) {
        if (minAge > maxAge) {
            throw new BusinessException("Minimum age cannot be greater than maximum age");
        }
    }
}