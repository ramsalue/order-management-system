package com.meli.ordermanagementsystem.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * Data Transfer Object for Item
 * Used for API requests and responses
 */
public class ItemDTO {

    private Long itemId;

    @NotBlank(message = "Item name is required")
    @Size(min = 2, max = 150, message = "Item name must be between 2 and 150 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;

    /**
     * Default constructor
     */
    public ItemDTO() {
    }

    /**
     * Constructor with all fields
     */
    public ItemDTO(Long itemId, String name, String description, BigDecimal price) {
        this.itemId = itemId;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    // Getters and Setters

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}