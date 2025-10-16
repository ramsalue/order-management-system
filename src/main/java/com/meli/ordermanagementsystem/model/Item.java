package com.meli.ordermanagementsystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Item entity representing products in the system
 * This class maps to the 'items' table in the database
 */
@Entity
@Table(name = "items")
public class Item {

    /**
     * Primary key for the item entity
     * Auto-generated using IDENTITY strategy
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long itemId;

    /**
     * Item's name
     * Cannot be null or blank
     */
    @NotBlank(message = "Item name is required")
    @Size(min = 2, max = 150, message = "Item name must be between 2 and 150 characters")
    @Column(name = "name", nullable = false, length = 150)
    private String name;

    /**
     * Item's description
     * Optional field providing additional details
     */
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    @Column(name = "description", length = 500)
    private String description;

    /**
     * Item's price
     * Must be positive value
     * Uses BigDecimal for precise monetary calculations
     */
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * Set of orders containing this item
     * Many-to-Many relationship with Order entity
     * mappedBy indicates Order entity owns the relationship
     */
    @ManyToMany(mappedBy = "items")
    private Set<Order> orders = new HashSet<>();

    /**
     * Default constructor required by JPA
     */
    public Item() {
    }

    /**
     * Constructor with all fields except ID and orders
     * @param name item's name
     * @param description item's description
     * @param price item's price
     */
    public Item(String name, String description, BigDecimal price) {
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

    public Set<Order> getOrders() {
        return orders;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }

    @Override
    public String toString() {
        return "Item{" +
                "itemId=" + itemId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                '}';
    }
}