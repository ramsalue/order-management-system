package com.meli.ordermanagementsystem.repository;

import com.meli.ordermanagementsystem.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Client entity
 * Provides data access operations for Client management
 * Extends JpaRepository to inherit basic CRUD operations
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    /**
     * Finds a client by exact name match
     * Spring Data JPA automatically generates implementation
     * @param name the client's name
     * @return Optional containing the client if found
     */
    Optional<Client> findByName(String name);

    /**
     * Finds clients whose names contain the given string (case-insensitive)
     * Useful for search functionality
     * @param name partial name to search
     * @return List of clients matching the search criteria
     */
    List<Client> findByNameContainingIgnoreCase(String name);

    /**
     * Finds clients by age
     * @param age the client's age
     * @return List of clients with the specified age
     */
    List<Client> findByAge(Integer age);

    /**
     * Finds clients within an age range
     * @param minAge minimum age (inclusive)
     * @param maxAge maximum age (inclusive)
     * @return List of clients within the age range
     */
    List<Client> findByAgeBetween(Integer minAge, Integer maxAge);

    /**
     * Finds clients by address containing specific text
     * @param address partial address to search
     * @return List of clients matching the address criteria
     */
    List<Client> findByAddressContainingIgnoreCase(String address);

    /**
     * Checks if a client exists with the given name
     * @param name the client's name
     * @return true if client exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Custom query to find clients with orders
     * Uses JPQL (Java Persistence Query Language)
     * @return List of clients who have placed at least one order
     */
    @Query("SELECT DISTINCT c FROM Client c JOIN c.orders o")
    List<Client> findClientsWithOrders();

    /**
     * Custom query to find clients without orders
     * Useful for marketing campaigns targeting inactive customers
     * @return List of clients who have not placed any orders
     */
    @Query("SELECT c FROM Client c WHERE c.orders IS EMPTY")
    List<Client> findClientsWithoutOrders();

    /**
     * Custom query to count orders for a specific client
     * @param clientId the client's ID
     * @return number of orders placed by the client
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.client.idClient = :clientId")
    Long countOrdersByClientId(@Param("clientId") Long clientId);
}