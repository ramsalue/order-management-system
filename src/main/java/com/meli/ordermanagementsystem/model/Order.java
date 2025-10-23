package com.meli.ordermanagementsystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Order entity representing purchase orders in the system
 * This class maps to the 'orders' table in the database
 */
@Entity
@Table(name = "orders")
public class Order {
    /**
     * Primary key for the order entity
     * Auto-generated using IDENTITY strategy
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_order")
    private Long idOrder;

    /**
     * Date when the order was purchased
     * Cannot be null
     */
    @NotNull(message = "Purchase date is required")
    @PastOrPresent(message = "Purchase date cannot be in the future")
    @Column(name = "purchase_date", nullable = false)
    private LocalDate purchaseDate;

    /**
     * Expected delivery date
     * Should be after or equal to purchase date
     */
    //@Future(message = "Delivery date must be in the future")
    @Column(name = "delivery_date")
    private LocalDate deliveryDate;

    /**
     * Order status
     * Possible values: PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED
     */
    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    /**
     * Client who placed this order
     * Many-to-One relationship with Client entity
     * Cannot be null
     */
    @NotNull(message = "Client is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_client", nullable = false)
    private Client client;

    /**
     * Set of items in this order
     * Many-to-Many relationship with Item entity
     * Uses junction table 'order_items'
     */
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE},fetch = FetchType.EAGER)
    @JoinTable(
            name = "order_items",
            joinColumns = @JoinColumn(name = "id_order"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private Set<Item> items = new HashSet<>();

    /**
     * Default constructor required by JPA
     */
    public Order() {
        this.status = OrderStatus.PENDING; // Default status
    }

    /**
     * Constructor with required fields
     *
     * @param purchaseDate date of purchase
     * @param client       client who placed the order
     */
    public Order(LocalDate purchaseDate, Client client) {
        this.purchaseDate = purchaseDate;
        this.client = client;
        this.status = OrderStatus.PENDING;
    }

    // Getters and Setters

    public Long getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(Long idOrder) {
        this.idOrder = idOrder;
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

    public void setDeliveryDate (LocalDate deliveryDate){
        this.deliveryDate = deliveryDate;
    }

    public OrderStatus getStatus () {
        return status;
    }

    public void setStatus (OrderStatus status){
        this.status = status;
    }

    public Client getClient () {
        return client;
    }

    public void setClient (Client client){
        this.client = client;
    }

    public Set<Item> getItems () {
        return items;
    }

    public void setItems (Set < Item > items) {
        this.items = items;
    }

    /**
     * Utility method to add an item to this order
     * Maintains bidirectional relationship
     * @param item the item to add
     */
    public void addItem (Item item){
        this.items.add(item);
        item.getOrders().add(this);
    }

    /**
     * Utility method to remove an item from this order
     * Maintains bidirectional relationship
     * @param item the item to remove
     */
    public void removeItem (Item item){
        this.items.remove(item);
        item.getOrders().remove(this);
    }

    /**
     * Utility method to remove all items from this order
     */
    public void removeAllItems () {
        for (Item item : new HashSet<>(items)) {
            removeItem(item);
        }
    }

    @Override
    public String toString () {
        return "Order{" +
                "idOrder=" + idOrder +
                ", purchaseDate=" + purchaseDate +
                ", deliveryDate=" + deliveryDate +
                ", status=" + status +
                ", clientId=" + (client != null ? client.getIdClient() : null) +
                ", itemCount=" + items.size() +
                '}';
    }
}


