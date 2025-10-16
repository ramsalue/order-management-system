package com.meli.ordermanagementsystem.controller;

import com.meli.ordermanagementsystem.dto.ClientDTO;
import com.meli.ordermanagementsystem.model.Client;
import com.meli.ordermanagementsystem.service.ClientService;
import com.meli.ordermanagementsystem.util.EntityMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for Client management
 * Handles HTTP requests for client operations
 * Base URL: /api/clients
 */
@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;

    /**
     * Constructor with dependency injection
     * @param clientService the client service
     */
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    /**
     * Creates a new client
     * POST /api/clients
     * @param clientDTO client data
     * @return ResponseEntity with created client and 201 status
     */
    @PostMapping
    public ResponseEntity<ClientDTO> createClient(@Valid @RequestBody ClientDTO clientDTO) {
        Client client = EntityMapper.toClientEntity(clientDTO);
        Client savedClient = clientService.createClient(client);
        ClientDTO responseDTO = EntityMapper.toClientDTO(savedClient);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    /**
     * Retrieves a client by ID
     * GET /api/clients/{id}
     * @param id the client ID
     * @return ResponseEntity with client data and 200 status
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClientDTO> getClientById(@PathVariable Long id) {
        Client client = clientService.getClientById(id);
        ClientDTO clientDTO = EntityMapper.toClientDTO(client);
        return ResponseEntity.ok(clientDTO);
    }

    /**
     * Retrieves all clients
     * GET /api/clients
     * @return ResponseEntity with list of clients and 200 status
     */
    @GetMapping
    public ResponseEntity<List<ClientDTO>> getAllClients() {
        List<Client> clients = clientService.getAllClients();
        List<ClientDTO> clientDTOs = clients.stream()
                .map(EntityMapper::toClientDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(clientDTOs);
    }

    /**
     * Searches clients by name
     * GET /api/clients/search?name={name}
     * @param name partial name to search
     * @return ResponseEntity with matching clients and 200 status
     */
    @GetMapping("/search")
    public ResponseEntity<List<ClientDTO>> searchClientsByName(@RequestParam String name) {
        List<Client> clients = clientService.searchClientsByName(name);
        List<ClientDTO> clientDTOs = clients.stream()
                .map(EntityMapper::toClientDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(clientDTOs);
    }

    /**
     * Gets clients by age range
     * GET /api/clients/age-range?minAge={min}&maxAge={max}
     * @param minAge minimum age
     * @param maxAge maximum age
     * @return ResponseEntity with matching clients and 200 status
     */
    @GetMapping("/age-range")
    public ResponseEntity<List<ClientDTO>> getClientsByAgeRange(
            @RequestParam Integer minAge,
            @RequestParam Integer maxAge) {
        List<Client> clients = clientService.getClientsByAgeRange(minAge, maxAge);
        List<ClientDTO> clientDTOs = clients.stream()
                .map(EntityMapper::toClientDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(clientDTOs);
    }

    /**
     * Updates an existing client
     * PUT /api/clients/{id}
     * @param id the client ID
     * @param clientDTO updated client data
     * @return ResponseEntity with updated client and 200 status
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClientDTO> updateClient(
            @PathVariable Long id,
            @Valid @RequestBody ClientDTO clientDTO) {
        Client clientDetails = EntityMapper.toClientEntity(clientDTO);
        Client updatedClient = clientService.updateClient(id, clientDetails);
        ClientDTO responseDTO = EntityMapper.toClientDTO(updatedClient);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Deletes a client by ID
     * DELETE /api/clients/{id}
     * @param id the client ID
     * @return ResponseEntity with 204 No Content status
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Gets clients who have orders
     * GET /api/clients/with-orders
     * @return ResponseEntity with clients and 200 status
     */
    @GetMapping("/with-orders")
    public ResponseEntity<List<ClientDTO>> getClientsWithOrders() {
        List<Client> clients = clientService.getClientsWithOrders();
        List<ClientDTO> clientDTOs = clients.stream()
                .map(EntityMapper::toClientDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(clientDTOs);
    }

    /**
     * Gets clients without orders
     * GET /api/clients/without-orders
     * @return ResponseEntity with clients and 200 status
     */
    @GetMapping("/without-orders")
    public ResponseEntity<List<ClientDTO>> getClientsWithoutOrders() {
        List<Client> clients = clientService.getClientsWithoutOrders();
        List<ClientDTO> clientDTOs = clients.stream()
                .map(EntityMapper::toClientDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(clientDTOs);
    }

    /**
     * Gets order count for a client
     * GET /api/clients/{id}/order-count
     * @param id the client ID
     * @return ResponseEntity with order count and 200 status
     */
    @GetMapping("/{id}/order-count")
    public ResponseEntity<Long> getClientOrderCount(@PathVariable Long id) {
        Long count = clientService.getClientOrderCount(id);
        return ResponseEntity.ok(count);
    }
}