package com.meli.ordermanagementsystem.dto;

import com.meli.ordermanagementsystem.model.OrderStatus;
import java.time.LocalDate;
import java.util.List;

/**
 * Response DTO for Order with full details
 * Includes client and items information
 */
public class OrderResponseDTO {

    private Long idOrder;
    private ClientDTO client;
    private LocalDate purchaseDate;
    private LocalDate deliveryDate;
    private OrderStatus status;
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