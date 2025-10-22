package com.meli.ordermanagementsystem.service;

import com.meli.ordermanagementsystem.exception.BusinessException;
import com.meli.ordermanagementsystem.exception.ResourceNotFoundException;
import com.meli.ordermanagementsystem.model.Client;
import com.meli.ordermanagementsystem.repository.ClientRepository;
import com.meli.ordermanagementsystem.service.ClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ClientService
 * Tests business logic without database dependencies
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ClientService Unit Tests")
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientService clientService;

    private Client testClient;

    /**
     * Set up test data before each test
     */
    @BeforeEach
    void setUp() {
        testClient = new Client("John Doe", "123 Main St", 30);
        testClient.setIdClient(1L);
    }

    // ========================================
    // CREATE CLIENT TESTS
    // ========================================

    @Test
    @DisplayName("Should create client successfully")
    void testCreateClient_Success() {
        // Given
        when(clientRepository.existsByName(testClient.getName())).thenReturn(false);
        when(clientRepository.save(any(Client.class))).thenReturn(testClient);

        // When
        Client createdClient = clientService.createClient(testClient);

        // Then
        assertThat(createdClient).isNotNull();
        assertThat(createdClient.getName()).isEqualTo("John Doe");
        assertThat(createdClient.getIdClient()).isEqualTo(1L);

        verify(clientRepository, times(1)).existsByName("John Doe");
        verify(clientRepository, times(1)).save(testClient);
    }

    @Test
    @DisplayName("Should throw BusinessException when client name already exists")
    void testCreateClient_DuplicateName() {
        // Given
        when(clientRepository.existsByName(testClient.getName())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> clientService.createClient(testClient))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already exists");

        verify(clientRepository, times(1)).existsByName("John Doe");
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when client age is less than 18")
    void testCreateClient_InvalidAge() {
        // Given
        Client underage = new Client("Minor Client", "456 Test Ave", 17);
        when(clientRepository.existsByName(underage.getName())).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> clientService.createClient(underage))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("at least 18");

        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when client age exceeds 120")
    void testCreateClient_AgeExceedsMaximum() {
        // Given
        Client tooOld = new Client("Very Old Client", "789 Elder Ln", 121);
        when(clientRepository.existsByName(tooOld.getName())).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> clientService.createClient(tooOld))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Invalid age");

        verify(clientRepository, never()).save(any(Client.class));
    }

    // ========================================
    // GET CLIENT TESTS
    // ========================================

    @Test
    @DisplayName("Should get client by ID successfully")
    void testGetClientById_Success() {
        // Given
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));

        // When
        Client foundClient = clientService.getClientById(1L);

        // Then
        assertThat(foundClient).isNotNull();
        assertThat(foundClient.getIdClient()).isEqualTo(1L);
        assertThat(foundClient.getName()).isEqualTo("John Doe");

        verify(clientRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when client not found")
    void testGetClientById_NotFound() {
        // Given
        when(clientRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> clientService.getClientById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("not found");

        verify(clientRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should get all clients successfully")
    void testGetAllClients_Success() {
        // Given
        Client client2 = new Client("Jane Smith", "456 Oak Ave", 28);
        client2.setIdClient(2L);
        List<Client> clients = Arrays.asList(testClient, client2);

        when(clientRepository.findAll()).thenReturn(clients);

        // When
        List<Client> foundClients = clientService.getAllClients();

        // Then
        assertThat(foundClients).hasSize(2);
        assertThat(foundClients).contains(testClient, client2);

        verify(clientRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no clients exist")
    void testGetAllClients_EmptyList() {
        // Given
        when(clientRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<Client> foundClients = clientService.getAllClients();

        // Then
        assertThat(foundClients).isEmpty();

        verify(clientRepository, times(1)).findAll();
    }

    // ========================================
    // SEARCH CLIENT TESTS
    // ========================================

    @Test
    @DisplayName("Should search clients by name successfully")
    void testSearchClientsByName_Success() {
        // Given
        when(clientRepository.findByNameContainingIgnoreCase("John"))
                .thenReturn(Arrays.asList(testClient));

        // When
        List<Client> foundClients = clientService.searchClientsByName("John");

        // Then
        assertThat(foundClients).hasSize(1);
        assertThat(foundClients.get(0).getName()).contains("John");

        verify(clientRepository, times(1)).findByNameContainingIgnoreCase("John");
    }

    @Test
    @DisplayName("Should get clients by age range successfully")
    void testGetClientsByAgeRange_Success() {
        // Given
        when(clientRepository.findByAgeBetween(25, 35))
                .thenReturn(Arrays.asList(testClient));

        // When
        List<Client> foundClients = clientService.getClientsByAgeRange(25, 35);

        // Then
        assertThat(foundClients).hasSize(1);
        assertThat(foundClients.get(0).getAge()).isBetween(25, 35);

        verify(clientRepository, times(1)).findByAgeBetween(25, 35);
    }

    @Test
    @DisplayName("Should throw BusinessException when age range is invalid")
    void testGetClientsByAgeRange_InvalidRange() {
        // When & Then
        assertThatThrownBy(() -> clientService.getClientsByAgeRange(35, 25))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("cannot be greater than");

        verify(clientRepository, never()).findByAgeBetween(anyInt(), anyInt());
    }

    // ========================================
    // UPDATE CLIENT TESTS
    // ========================================

    @Test
    @DisplayName("Should update client successfully")
    void testUpdateClient_Success() {
        // Given
        Client updatedData = new Client("John Doe Updated", "789 New St", 31);
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(clientRepository.existsByName("John Doe Updated")).thenReturn(false);
        when(clientRepository.save(any(Client.class))).thenReturn(testClient);

        // When
        Client updatedClient = clientService.updateClient(1L, updatedData);

        // Then
        assertThat(updatedClient).isNotNull();
        verify(clientRepository, times(1)).findById(1L);
        verify(clientRepository, times(1)).save(any(Client.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when updating to duplicate name")
    void testUpdateClient_DuplicateName() {
        // Given
        Client updatedData = new Client("Jane Smith", "789 New St", 31);
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(clientRepository.existsByName("Jane Smith")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> clientService.updateClient(1L, updatedData))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already exists");

        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent client")
    void testUpdateClient_NotFound() {
        // Given
        Client updatedData = new Client("New Name", "New Address", 30);
        when(clientRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> clientService.updateClient(999L, updatedData))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(clientRepository, never()).save(any(Client.class));
    }

    // ========================================
    // DELETE CLIENT TESTS
    // ========================================

    @Test
    @DisplayName("Should delete client successfully when no orders exist")
    void testDeleteClient_Success() {
        // Given
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(clientRepository.countOrdersByClientId(1L)).thenReturn(0L);
        doNothing().when(clientRepository).delete(testClient);

        // When
        clientService.deleteClient(1L);

        // Then
        verify(clientRepository, times(1)).findById(1L);
        verify(clientRepository, times(1)).countOrdersByClientId(1L);
        verify(clientRepository, times(1)).delete(testClient);
    }

    @Test
    @DisplayName("Should throw BusinessException when deleting client with orders")
    void testDeleteClient_HasOrders() {
        // Given
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(clientRepository.countOrdersByClientId(1L)).thenReturn(5L);

        // When & Then
        assertThatThrownBy(() -> clientService.deleteClient(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("existing orders");

        verify(clientRepository, never()).delete(any(Client.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent client")
    void testDeleteClient_NotFound() {
        // Given
        when(clientRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> clientService.deleteClient(999L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(clientRepository, never()).delete(any(Client.class));
    }

    // ========================================
    // BUSINESS LOGIC TESTS
    // ========================================

    @Test
    @DisplayName("Should get clients with orders successfully")
    void testGetClientsWithOrders_Success() {
        // Given
        when(clientRepository.findClientsWithOrders()).thenReturn(Arrays.asList(testClient));

        // When
        List<Client> clients = clientService.getClientsWithOrders();

        // Then
        assertThat(clients).hasSize(1);
        verify(clientRepository, times(1)).findClientsWithOrders();
    }

    @Test
    @DisplayName("Should get clients without orders successfully")
    void testGetClientsWithoutOrders_Success() {
        // Given
        when(clientRepository.findClientsWithoutOrders()).thenReturn(Arrays.asList(testClient));

        // When
        List<Client> clients = clientService.getClientsWithoutOrders();

        // Then
        assertThat(clients).hasSize(1);
        verify(clientRepository, times(1)).findClientsWithoutOrders();
    }

    @Test
    @DisplayName("Should get client order count successfully")
    void testGetClientOrderCount_Success() {
        // Given
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(clientRepository.countOrdersByClientId(1L)).thenReturn(3L);

        // When
        Long count = clientService.getClientOrderCount(1L);

        // Then
        assertThat(count).isEqualTo(3L);
        verify(clientRepository, times(1)).findById(1L);
        verify(clientRepository, times(1)).countOrdersByClientId(1L);
    }
}