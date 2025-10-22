package com.meli.ordermanagementsystem.dto;

import com.meli.ordermanagementsystem.model.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

/**
 * Data Transfer Object for Order
 * Used for API requests and responses
 */
@Schema(description = "Order request information for creating or updating orders")
public class OrderDTO {

    @Schema(description = "Unique identifier of the order",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Long idOrder;

    @NotNull(message = "Client ID is required")
    @Schema(description = "ID of the client placing the order",
            example = "1",
            required = true)
    private Long clientId;

    @NotNull(message = "Purchase date is required")
    @PastOrPresent(message = "Purchase date cannot be in the future")
    @Schema(description = "Date when the order was placed",
            example = "2025-10-17",
            required = true,
            type = "string",
            format = "date")
    private LocalDate purchaseDate;

    @Future(message = "Delivery date must be in the future")
    @Schema(description = "Expected delivery date",
            example = "2025-10-24",
            type = "string",
            format = "date")
    private LocalDate deliveryDate;

    @Schema(description = "Current status of the order",
            example = "PENDING",
            allowableValues = {"PENDING", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED"})
    private OrderStatus status;

    @NotEmpty(message = "Order must contain at least one item")
    @Schema(description = "List of item IDs included in the order",
            example = "[1, 2, 3]",
            required = true)
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