package com.meli.ordermanagementsystem.dto;

import com.meli.ordermanagementsystem.model.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

/**
 * Response DTO for Order with full details
 * Includes client and items information
 */
@Schema(description = "Complete order information including client and items details")
public class OrderResponseDTO {
    @Schema(description = "Unique identifier of the order", example = "1")
    private Long idOrder;
    @Schema(description = "Client information who placed the order")
    private ClientDTO client;
    @Schema(description = "Date when the order was placed",
            example = "2025-10-17",
            type = "string",
            format = "date")
    private LocalDate purchaseDate;
    @Schema(description = "Expected delivery date",
            example = "2025-10-24",
            type = "string",
            format = "date")
    private LocalDate deliveryDate;
    @Schema(description = "Current status of the order",
            example = "PENDING")
    private OrderStatus status;
    @Schema(description = "List of items included in the order")
    private List<ItemDTO> items;

    /**
     * Default constructor
     */
    public OrderResponseDTO() {
    }

    /**
     * Constructor with all fields
     */
    public OrderResponseDTO(Long idOrder, ClientDTO client, LocalDate purchaseDate,
                            LocalDate deliveryDate, OrderStatus status, List<ItemDTO> items) {
        this.idOrder = idOrder;
        this.client = client;
        this.purchaseDate = purchaseDate;
        this.deliveryDate = deliveryDate;
        this.status = status;
        this.items = items;
    }

    // Getters and Setters

    public Long getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(Long idOrder) {
        this.idOrder = idOrder;
    }

    public ClientDTO getClient() {
        return client;
    }

    public void setClient(ClientDTO client) {
        this.client = client;
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

    public List<ItemDTO> getItems() {
        return items;
    }

    public void setItems(List<ItemDTO> items) {
        this.items = items;
    }
}