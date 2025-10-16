package com.meli.ordermanagementsystem.repository;

import com.meli.ordermanagementsystem.model.Order;
import com.meli.ordermanagementsystem.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for Order entity
 * Provides data access operations for Order management
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Finds all orders for a specific client
     * @param clientId the client's ID
     * @return List of orders belonging to the client
     */
    List<Order> findByClientIdClient(Long clientId);

    /**
     * Finds orders by status
     * @param status the order status
     * @return List of orders with the specified status
     */
    List<Order> findByStatus(OrderStatus status);

    /**
     * Finds orders by purchase date
     * @param purchaseDate the date of purchase
     * @return List of orders placed on the specified date
     */
    List<Order> findByPurchaseDate(LocalDate purchaseDate);

    /**
     * Finds orders within a date range
     * @param startDate start date (inclusive)
     * @param endDate end date (inclusive)
     * @return List of orders within the date range
     */
    List<Order> findByPurchaseDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Finds orders by delivery date
     * @param deliveryDate the expected delivery date
     * @return List of orders scheduled for delivery on the specified date
     */
    List<Order> findByDeliveryDate(LocalDate deliveryDate);

    /**
     * Finds orders for a specific client with a specific status
     * @param clientId the client's ID
     * @param status the order status
     * @return List of matching orders
     */
    List<Order> findByClientIdClientAndStatus(Long clientId, OrderStatus status);

    /**
     * Finds orders placed after a specific date
     * @param date the reference date
     * @return List of orders placed after the date
     */
    List<Order> findByPurchaseDateAfter(LocalDate date);

    /**
     * Finds orders placed before a specific date
     * @param date the reference date
     * @return List of orders placed before the date
     */
    List<Order> findByPurchaseDateBefore(LocalDate date);

    /**
     * Checks if a client has any orders
     * @param clientId the client's ID
     * @return true if client has orders, false otherwise
     */
    boolean existsByClientIdClient(Long clientId);

    /**
     * Custom query to find orders with multiple items
     * @param minItems minimum number of items
     * @return List of orders containing at least minItems items
     */
    @Query("SELECT o FROM Order o WHERE SIZE(o.items) >= :minItems")
    List<Order> findOrdersWithMinimumItems(@Param("minItems") int minItems);

    /**
     * Custom query to find orders containing a specific item
     * @param itemId the item's ID
     * @return List of orders containing the specified item
     */
    @Query("SELECT o FROM Order o JOIN o.items i WHERE i.itemId = :itemId")
    List<Order> findOrdersContainingItem(@Param("itemId") Long itemId);

    /**
     * Custom query to count orders by status
     * @param status the order status
     * @return number of orders with the specified status
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    Long countByStatus(@Param("status") OrderStatus status);

    /**
     * Custom query to find recent orders (last N days)
     * @param cutoffDate number of days to look back
     * @return List of recent orders
     */
    @Query("SELECT o FROM Order o WHERE o.purchaseDate >= :cutoffDate ORDER BY o.purchaseDate DESC")
    List<Order> findRecentOrders(@Param("cutoffDate") LocalDate cutoffDate);

    /**
     * Custom query to find orders pending delivery
     * Orders that are shipped but delivery date is in the future
     * @return List of orders in transit
     */
    @Query("SELECT o FROM Order o WHERE o.status = 'SHIPPED' AND o.deliveryDate > CURRENT_DATE")
    List<Order> findOrdersInTransit();

    /**
     * Custom query to find overdue orders
     * Orders where delivery date has passed but status is not delivered
     * @return List of overdue orders
     */
    @Query("SELECT o FROM Order o WHERE o.deliveryDate < CURRENT_DATE AND o.status != 'DELIVERED' AND o.status != 'CANCELLED'")
    List<Order> findOverdueOrders();

    /**
     * Custom query to calculate total orders for a client
     * @param clientId the client's ID
     * @return total number of orders
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.client.idClient = :clientId")
    Long countOrdersByClient(@Param("clientId") Long clientId);
}