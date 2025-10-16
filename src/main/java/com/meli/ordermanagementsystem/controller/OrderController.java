package com.meli.ordermanagementsystem.controller;

import com.meli.ordermanagementsystem.dto.OrderDTO;
import com.meli.ordermanagementsystem.dto.OrderResponseDTO;
import com.meli.ordermanagementsystem.model.Order;
import com.meli.ordermanagementsystem.model.OrderStatus;
import com.meli.ordermanagementsystem.service.OrderService;
import com.meli.ordermanagementsystem.util.EntityMapper;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * REST Controller for Order management
 * Handles HTTP requests for order operations
 * Base URL: /api/orders
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    /**
     * Constructor with dependency injection
     * @param orderService the order service
     */
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Creates a new order
     * POST /api/orders
     * @param orderDTO order data including clientId and itemIds
     * @return ResponseEntity with created order and 201 status
     */
    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody OrderDTO orderDTO) {
        Order order = EntityMapper.toOrderEntity(orderDTO);
        Order savedOrder = orderService.createOrder(
                order,
                orderDTO.getClientId(),
                orderDTO.getItemIds()
        );
        OrderResponseDTO responseDTO = EntityMapper.toOrderResponseDTO(savedOrder);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    /**
     * Retrieves an order by ID
     * GET /api/orders/{id}
     * @param id the order ID
     * @return ResponseEntity with order data including client and items, 200 status
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        OrderResponseDTO orderResponseDTO = EntityMapper.toOrderResponseDTO(order);
        return ResponseEntity.ok(orderResponseDTO);
    }

    /**
     * Retrieves all orders
     * GET /api/orders
     * @return ResponseEntity with list of orders and 200 status
     */
    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        List<OrderResponseDTO> orderDTOs = orders.stream()
                .map(EntityMapper::toOrderResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orderDTOs);
    }

    /**
     * Gets orders for a specific client
     * GET /api/orders/client/{clientId}
     * @param clientId the client ID
     * @return ResponseEntity with client's orders and 200 status
     */
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByClient(@PathVariable Long clientId) {
        List<Order> orders = orderService.getOrdersByClient(clientId);
        List<OrderResponseDTO> orderDTOs = orders.stream()
                .map(EntityMapper::toOrderResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orderDTOs);
    }

    /**
     * Gets orders by status
     * GET /api/orders/status/{status}
     * @param status the order status (PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED)
     * @return ResponseEntity with matching orders and 200 status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByStatus(@PathVariable OrderStatus status) {
        List<Order> orders = orderService.getOrdersByStatus(status);
        List<OrderResponseDTO> orderDTOs = orders.stream()
                .map(EntityMapper::toOrderResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orderDTOs);
    }

    /**
     * Gets orders within a date range
     * GET /api/orders/date-range?startDate={start}&endDate={end}
     * @param startDate start date in format yyyy-MM-dd
     * @param endDate end date in format yyyy-MM-dd
     * @return ResponseEntity with matching orders and 200 status
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Order> orders = orderService.getOrdersByDateRange(startDate, endDate);
        List<OrderResponseDTO> orderDTOs = orders.stream()
                .map(EntityMapper::toOrderResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orderDTOs);
    }

    /**
     * Gets recent orders (last N days)
     * GET /api/orders/recent?days={n}
     * @param days number of days to look back
     * @return ResponseEntity with recent orders and 200 status
     */
    @GetMapping("/recent")
    public ResponseEntity<List<OrderResponseDTO>> getRecentOrders(@RequestParam int days) {
        List<Order> orders = orderService.getRecentOrders(days);
        List<OrderResponseDTO> orderDTOs = orders.stream()
                .map(EntityMapper::toOrderResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orderDTOs);
    }

    /**
     * Updates an existing order
     * PUT /api/orders/{id}
     * @param id the order ID
     * @param orderDTO updated order data
     * @return ResponseEntity with updated order and 200 status
     */
    @PutMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> updateOrder(
            @PathVariable Long id,
            @Valid @RequestBody OrderDTO orderDTO) {
        Order orderDetails = EntityMapper.toOrderEntity(orderDTO);
        Order updatedOrder = orderService.updateOrder(id, orderDetails);
        OrderResponseDTO responseDTO = EntityMapper.toOrderResponseDTO(updatedOrder);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Updates order status
     * PATCH /api/orders/{id}/status
     * @param id the order ID
     * @param status the new status
     * @return ResponseEntity with updated order and 200 status
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {
        Order updatedOrder = orderService.updateOrderStatus(id, status);
        OrderResponseDTO responseDTO = EntityMapper.toOrderResponseDTO(updatedOrder);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Adds items to an existing order
     * POST /api/orders/{id}/items
     * @param id the order ID
     * @param itemIds set of item IDs to add
     * @return ResponseEntity with updated order and 200 status
     */
    @PostMapping("/{id}/items")
    public ResponseEntity<OrderResponseDTO> addItemsToOrder(
            @PathVariable Long id,
            @RequestBody Set<Long> itemIds) {
        Order updatedOrder = orderService.addItemsToOrder(id, itemIds);
        OrderResponseDTO responseDTO = EntityMapper.toOrderResponseDTO(updatedOrder);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Removes an item from an order
     * DELETE /api/orders/{orderId}/items/{itemId}
     * @param orderId the order ID
     * @param itemId the item ID to remove
     * @return ResponseEntity with updated order and 200 status
     */
    @DeleteMapping("/{orderId}/items/{itemId}")
    public ResponseEntity<OrderResponseDTO> removeItemFromOrder(
            @PathVariable Long orderId,
            @PathVariable Long itemId) {
        Order updatedOrder = orderService.removeItemFromOrder(orderId, itemId);
        OrderResponseDTO responseDTO = EntityMapper.toOrderResponseDTO(updatedOrder);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Cancels an order
     * POST /api/orders/{id}/cancel
     * @param id the order ID
     * @return ResponseEntity with cancelled order and 200 status
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderResponseDTO> cancelOrder(@PathVariable Long id) {
        Order cancelledOrder = orderService.cancelOrder(id);
        OrderResponseDTO responseDTO = EntityMapper.toOrderResponseDTO(cancelledOrder);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Deletes an order by ID
     * DELETE /api/orders/{id}
     * @param id the order ID
     * @return ResponseEntity with 204 No Content status
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Gets orders in transit
     * GET /api/orders/in-transit
     * @return ResponseEntity with orders in transit and 200 status
     */
    @GetMapping("/in-transit")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersInTransit() {
        List<Order> orders = orderService.getOrdersInTransit();
        List<OrderResponseDTO> orderDTOs = orders.stream()
                .map(EntityMapper::toOrderResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orderDTOs);
    }

    /**
     * Gets overdue orders
     * GET /api/orders/overdue
     * @return ResponseEntity with overdue orders and 200 status
     */
    @GetMapping("/overdue")
    public ResponseEntity<List<OrderResponseDTO>> getOverdueOrders() {
        List<Order> orders = orderService.getOverdueOrders();
        List<OrderResponseDTO> orderDTOs = orders.stream()
                .map(EntityMapper::toOrderResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orderDTOs);
    }

    /**
     * Gets orders containing a specific item
     * GET /api/orders/containing-item/{itemId}
     * @param itemId the item ID
     * @return ResponseEntity with matching orders and 200 status
     */
    @GetMapping("/containing-item/{itemId}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersContainingItem(@PathVariable Long itemId) {
        List<Order> orders = orderService.getOrdersContainingItem(itemId);
        List<OrderResponseDTO> orderDTOs = orders.stream()
                .map(EntityMapper::toOrderResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orderDTOs);
    }

    /**
     * Counts orders by status
     * GET /api/orders/count-by-status?status={status}
     * @param status the order status
     * @return ResponseEntity with count and 200 status
     */
    @GetMapping("/count-by-status")
    public ResponseEntity<Long> countOrdersByStatus(@RequestParam OrderStatus status) {
        Long count = orderService.countOrdersByStatus(status);
        return ResponseEntity.ok(count);
    }
}