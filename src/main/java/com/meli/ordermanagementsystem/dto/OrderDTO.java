package com.meli.ordermanagementsystem.dto;

import com.meli.ordermanagementsystem.model.OrderStatus;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

/**
 * Data Transfer Object for Order
 * Used for API requests and responses
 */
public class OrderDTO {

    private Long idOrder;

    @NotNull(message = "Client ID is required")
    private Long clientId;

    @NotNull(message = "Purchase date is required")
    @PastOrPresent(message = "Purchase date cannot be in the future")
    private LocalDate purchaseDate;

    @Future(message = "Delivery date must be in the future")
    private LocalDate deliveryDate;

    private OrderStatus status;

    @NotEmpty(message = "Order must contain at least one item")
    private Set<Long> itemIds;

    /**
     * Default constructor
     */
    public OrderDTO() {
    }

    /**
     * Constructor with all fields
     */
    public OrderDTO(Long idOrder, Long clientId, LocalDate purchaseDate,
                    LocalDate deliveryDate, OrderStatus status, Set<Long> itemIds) {
        this.idOrder = idOrder;
        this.clientId = clientId;
        this.purchaseDate = purchaseDate;
        this.deliveryDate = deliveryDate;
        this.status = status;
        this.itemIds = itemIds;
    }

    // Getters and Setters

    public Long getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(Long idOrder) {
        this.idOrder = idOrder;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Set<Long> getItemIds() {
        return itemIds;
    }

    public void setItemIds(Set<Long> itemIds) {
        this.itemIds = itemIds;
    }
}