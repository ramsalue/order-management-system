package com.meli.ordermanagementsystem.service;

import com.meli.ordermanagementsystem.exception.BusinessException;
import com.meli.ordermanagementsystem.exception.ResourceNotFoundException;
import com.meli.ordermanagementsystem.model.Client;
import com.meli.ordermanagementsystem.model.Item;
import com.meli.ordermanagementsystem.model.Order;
import com.meli.ordermanagementsystem.model.OrderStatus;
import com.meli.ordermanagementsystem.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * Service class for Order business logic
 * Handles all order-related operations and validations
 */
@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final ClientService clientService;
    private final ItemService itemService;

    /**
     * Constructor with dependency injection
     * @param orderRepository the order repository
     * @param clientService the client service
     * @param itemService the item service
     */
    public OrderService(OrderRepository orderRepository,
                        ClientService clientService,
                        ItemService itemService) {
        this.orderRepository = orderRepository;
        this.clientService = clientService;
        this.itemService = itemService;
    }

    /**
     * Creates a new order
     * Validates client exists and items exist
     * @param order the order to create
     * @param clientId the client ID
     * @param itemIds set of item IDs to add to order
     * @return the created order with generated ID
     * @throws ResourceNotFoundException if client or items not found
     * @throws BusinessException if validation fails
     */
    public Order createOrder(Order order, Long clientId, Set<Long> itemIds) {
        // Validate and get client
        Client client = clientService.getClientById(clientId);
        order.setClient(client);

        // Validate purchase date
        if (order.getPurchaseDate() == null) {
            order.setPurchaseDate(LocalDate.now());
        }
        validatePurchaseDate(order.getPurchaseDate());

        // Validate delivery date if provided
        if (order.getDeliveryDate() != null) {
            validateDeliveryDate(order.getPurchaseDate(), order.getDeliveryDate());
        }

        // Set default status if not provided
        if (order.getStatus() == null) {
            order.setStatus(OrderStatus.PENDING);
        }

        // Validate and add items
        if (itemIds == null || itemIds.isEmpty()) {
            throw new BusinessException("Order must contain at least one item");
        }

        for (Long itemId : itemIds) {
            Item item = itemService.getItemById(itemId);
            order.addItem(item);
        }

        return orderRepository.save(order);
    }

    /**
     * Retrieves an order by ID
     * @param id the order ID
     * @return the order
     * @throws ResourceNotFoundException if order not found
     */
    @Transactional(readOnly = true)
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
    }

    /**
     * Retrieves all orders
     * @return list of all orders
     */
    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    /**
     * Gets orders for a specific client
     * @param clientId the client ID
     * @return list of client's orders
     */
    @Transactional(readOnly = true)
    public List<Order> getOrdersByClient(Long clientId) {
        clientService.getClientById(clientId); // Verify client exists
        return orderRepository.findByClientIdClient(clientId);
    }

    /**
     * Gets orders by status
     * @param status the order status
     * @return list of orders with the specified status
     */
    @Transactional(readOnly = true)
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    /**
     * Gets orders within a date range
     * @param startDate start date
     * @param endDate end date
     * @return list of orders in date range
     */
    @Transactional(readOnly = true)
    public List<Order> getOrdersByDateRange(LocalDate startDate, LocalDate endDate) {
        validateDateRange(startDate, endDate);
        return orderRepository.findByPurchaseDateBetween(startDate, endDate);
    }

    /**
     * Gets recent orders (last N days)
     * @param days number of days to look back
     * @return list of recent orders
     */
    @Transactional(readOnly = true)
    public List<Order> getRecentOrders(int days) {
        if (days <= 0) {
            throw new BusinessException("Days must be greater than 0");
        }
        LocalDate cutoffDate = LocalDate.now().minusDays(days);
        return orderRepository.findRecentOrders(cutoffDate);
    }

    /**
     * Updates an existing order
     * Only allows updates if order is not completed
     * @param id the order ID
     * @param orderDetails the updated order data
     * @return the updated order
     * @throws ResourceNotFoundException if order not found
     * @throws BusinessException if order cannot be updated
     */
    public Order updateOrder(Long id, Order orderDetails) {
        Order order = getOrderById(id);

        // Business rule: cannot update completed orders
        if (order.getStatus().isCompleted()) {
            throw new BusinessException("Cannot update completed order. Status: " + order.getStatus());
        }

        // Validate dates if being changed
        if (orderDetails.getPurchaseDate() != null) {
            validatePurchaseDate(orderDetails.getPurchaseDate());
            order.setPurchaseDate(orderDetails.getPurchaseDate());
        }

        if (orderDetails.getDeliveryDate() != null) {
            validateDeliveryDate(order.getPurchaseDate(), orderDetails.getDeliveryDate());
            order.setDeliveryDate(orderDetails.getDeliveryDate());
        }

        return orderRepository.save(order);
    }

    /**
     * Updates order status
     * Validates status transition is allowed
     * @param id the order ID
     * @param newStatus the new status
     * @return the updated order
     * @throws ResourceNotFoundException if order not found
     * @throws BusinessException if status transition is invalid
     */
    public Order updateOrderStatus(Long id, OrderStatus newStatus) {
        Order order = getOrderById(id);

        // Validate status transition
        validateStatusTransition(order.getStatus(), newStatus);

        order.setStatus(newStatus);

        // If marking as delivered, set delivery date to today if not set
        if (newStatus == OrderStatus.DELIVERED && order.getDeliveryDate() == null) {
            order.setDeliveryDate(LocalDate.now());
        }

        return orderRepository.save(order);
    }

    /**
     * Adds items to an existing order
     * Only allows if order is not completed
     * @param orderId the order ID
     * @param itemIds set of item IDs to add
     * @return the updated order
     */
    public Order addItemsToOrder(Long orderId, Set<Long> itemIds) {
        Order order = getOrderById(orderId);

        // Business rule: cannot modify completed orders
        if (order.getStatus().isCompleted()) {
            throw new BusinessException("Cannot add items to completed order");
        }

        if (itemIds == null || itemIds.isEmpty()) {
            throw new BusinessException("Must provide at least one item to add");
        }

        for (Long itemId : itemIds) {
            Item item = itemService.getItemById(itemId);
            order.addItem(item);
        }

        return orderRepository.save(order);
    }

    /**
     * Removes an item from an order
     * Only allows if order is not completed and has more than one item
     * @param orderId the order ID
     * @param itemId the item ID to remove
     * @return the updated order
     */
    public Order removeItemFromOrder(Long orderId, Long itemId) {
        Order order = getOrderById(orderId);

        // Business rule: cannot modify completed orders
        if (order.getStatus().isCompleted()) {
            throw new BusinessException("Cannot remove items from completed order");
        }

        // Business rule: order must have at least one item
        if (order.getItems().size() <= 1) {
            throw new BusinessException("Order must contain at least one item");
        }

        Item item = itemService.getItemById(itemId);
        order.removeItem(item);

        return orderRepository.save(order);
    }

    /**
     * Cancels an order
     * Only allows cancellation if order is cancellable
     * @param id the order ID
     * @return the cancelled order
     * @throws ResourceNotFoundException if order not found
     * @throws BusinessException if order cannot be cancelled
     */
    public Order cancelOrder(Long id) {
        Order order = getOrderById(id);

        // Business rule: can only cancel pending or processing orders
        if (!order.getStatus().isCancellable()) {
            throw new BusinessException("Cannot cancel order with status: " + order.getStatus());
        }

        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    /**
     * Deletes an order by ID
     * Only allows deletion if order is cancelled or pending
     * @param id the order ID
     * @throws ResourceNotFoundException if order not found
     * @throws BusinessException if order cannot be deleted
     */
    public void deleteOrder(Long id) {
        Order order = getOrderById(id);

        // Business rule: can only delete cancelled or pending orders
        if (order.getStatus() != OrderStatus.CANCELLED && order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException("Can only delete cancelled or pending orders. Current status: " + order.getStatus());
        }

        orderRepository.delete(order);
    }

    /**
     * Gets orders in transit (shipped but not delivered)
     * @return list of orders in transit
     */
    @Transactional(readOnly = true)
    public List<Order> getOrdersInTransit() {
        return orderRepository.findOrdersInTransit();
    }

    /**
     * Gets overdue orders (delivery date passed but not delivered)
     * @return list of overdue orders
     */
    @Transactional(readOnly = true)
    public List<Order> getOverdueOrders() {
        return orderRepository.findOverdueOrders();
    }

    /**
     * Gets orders containing a specific item
     * @param itemId the item ID
     * @return list of orders containing the item
     */
    @Transactional(readOnly = true)
    public List<Order> getOrdersContainingItem(Long itemId) {
        itemService.getItemById(itemId); // Verify item exists
        return orderRepository.findOrdersContainingItem(itemId);
    }

    /**
     * Counts orders by status
     * @param status the order status
     * @return count of orders with the status
     */
    @Transactional(readOnly = true)
    public Long countOrdersByStatus(OrderStatus status) {
        return orderRepository.countByStatus(status);
    }

    /**
     * Private helper method to validate purchase date
     * @param purchaseDate the purchase date
     * @throws BusinessException if date is invalid
     */
    private void validatePurchaseDate(LocalDate purchaseDate) {
        if (purchaseDate == null) {
            throw new BusinessException("Purchase date cannot be null");
        }
        if (purchaseDate.isAfter(LocalDate.now())) {
            throw new BusinessException("Purchase date cannot be in the future");
        }
    }

    /**
     * Private helper method to validate delivery date
     * @param purchaseDate the purchase date
     * @param deliveryDate the delivery date
     * @throws BusinessException if date is invalid
     */
    private void validateDeliveryDate(LocalDate purchaseDate, LocalDate deliveryDate) {
        if (deliveryDate == null) {
            throw new BusinessException("Delivery date cannot be null");
        }
        if (deliveryDate.isBefore(purchaseDate)) {
            throw new BusinessException("Delivery date cannot be before purchase date");
        }
        // Optional: validate delivery date is not too far in future
        LocalDate maxDeliveryDate = LocalDate.now().plusMonths(6);
        if (deliveryDate.isAfter(maxDeliveryDate)) {
            throw new BusinessException("Delivery date cannot be more than 6 months in the future");
        }
    }

    /**
     * Private helper method to validate date range
     * @param startDate start date
     * @param endDate end date
     * @throws BusinessException if range is invalid
     */
    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new BusinessException("Start date and end date cannot be null");
        }
        if (startDate.isAfter(endDate)) {
            throw new BusinessException("Start date cannot be after end date");
        }
    }

    /**
     * Private helper method to validate status transition
     * @param currentStatus current order status
     * @param newStatus new order status
     * @throws BusinessException if transition is invalid
     */
    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        // Cannot change from completed status
        if (currentStatus.isCompleted()) {
            throw new BusinessException("Cannot change status of completed order. Current status: " + currentStatus);
        }

        // Define valid transitions
        boolean isValidTransition = false;

        switch (currentStatus) {
            case PENDING:
                isValidTransition = (newStatus == OrderStatus.PROCESSING ||
                        newStatus == OrderStatus.CANCELLED);
                break;
            case PROCESSING:
                isValidTransition = (newStatus == OrderStatus.SHIPPED ||
                        newStatus == OrderStatus.CANCELLED);
                break;
            case SHIPPED:
                isValidTransition = (newStatus == OrderStatus.DELIVERED);
                break;
            default:
                isValidTransition = false;
        }

        if (!isValidTransition) {
            throw new BusinessException("Invalid status transition from " + currentStatus + " to " + newStatus);
        }
    }
}