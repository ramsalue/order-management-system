package com.meli.ordermanagementsystem.controller;

import com.meli.ordermanagementsystem.dto.ClientDTO;
import com.meli.ordermanagementsystem.model.Client;
import com.meli.ordermanagementsystem.service.ClientService;
import com.meli.ordermanagementsystem.util.EntityMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Clients", description = "Client management operations")
public class ClientController {

    private final ClientService clientService;

    /**
     * Constructor with dependency injection
     * @param clientService the client service
     */
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }
    @Operation(
            summary = "Create a new client",
            description = "Creates a new client in the system. The client name must be unique and age must be 18 or older."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Client created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ClientDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data or client name already exists",
                    content = @Content(mediaType = "application/json")
            )
    })
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
    @Operation(
            summary = "Get client by ID",
            description = "Retrieves a specific client by their unique identifier"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Client found and returned successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ClientDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Client not found with the provided ID",
                    content = @Content(mediaType = "application/json")
            )
    })
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
    @Operation(
            summary = "Get all clients",
            description = "Retrieves a list of all clients in the system"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of clients returned successfully (may be empty)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ClientDTO.class)
                    )
            )
    })
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

    @Operation(
            summary = "Search clients by name",
            description = "Searches for clients whose names contain the provided search term (case-insensitive)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Search completed successfully, returns matching clients",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ClientDTO.class)
                    )
            )
    })
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

    @Operation(
            summary = "Get clients by age range",
            description = "Retrieves clients whose ages fall within the specified range (inclusive)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Clients within age range returned successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ClientDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid age range (minAge greater than maxAge)",
                    content = @Content(mediaType = "application/json")
            )
    })
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

    @Operation(
            summary = "Update an existing client",
            description = "Updates the information of an existing client. The new name must be unique if changed."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Client updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ClientDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Client not found with the provided ID",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data or duplicate client name",
                    content = @Content(mediaType = "application/json")
            )
    })
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

    @Operation(
            summary = "Delete a client",
            description = "Deletes a client from the system. Cannot delete clients who have existing orders."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Client deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Client not found with the provided ID",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Cannot delete client with existing orders",
                    content = @Content(mediaType = "application/json")
            )
    })
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

    @Operation(
            summary = "Get clients with orders",
            description = "Retrieves all clients who have placed at least one order"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of clients with orders returned successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ClientDTO.class)
                    )
            )
    })
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

    @Operation(
            summary = "Get clients without orders",
            description = "Retrieves all clients who have not placed any orders yet"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of clients without orders returned successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ClientDTO.class)
                    )
            )
    })
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

    @Operation(
            summary = "Get client order count",
            description = "Returns the total number of orders placed by a specific client"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Order count returned successfully",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Client not found with the provided ID",
                    content = @Content(mediaType = "application/json")
            )
    })
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