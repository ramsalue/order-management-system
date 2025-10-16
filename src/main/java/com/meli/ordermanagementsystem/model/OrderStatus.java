package com.meli.ordermanagementsystem.model;

/**
 * Enumeration representing the possible states of an order
 * Used to track order lifecycle from creation to completion
 */
public enum OrderStatus {

    /**
     * Order has been created but not yet processed
     * Initial state for new orders
     */
    PENDING("Pending"),

    /**
     * Order is being prepared and processed
     * Intermediate state while order is being fulfilled
     */
    PROCESSING("Processing"),

    /**
     * Order has been shipped and is in transit
     * Customer can track delivery
     */
    SHIPPED("Shipped"),

    /**
     * Order has been successfully delivered to customer
     * Final successful state
     */
    DELIVERED("Delivered"),

    /**
     * Order has been cancelled
     * Final cancelled state
     */
    CANCELLED("Cancelled");

    private final String displayName;

    /**
     * Constructor for OrderStatus enum
     * @param displayName human-readable name for the status
     */
    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the display name of the status
     * @return display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Checks if this status represents a completed order
     * @return true if order is delivered or cancelled
     */
    public boolean isCompleted() {
        return this == DELIVERED || this == CANCELLED;
    }

    /**
     * Checks if order can be cancelled from this status
     * @return true if order is pending or processing
     */
    public boolean isCancellable() {
        return this == PENDING || this == PROCESSING;
    }

    /**
     * Checks if order can be updated from this status
     * @return true if order is not completed
     */
    public boolean isUpdatable() {
        return !isCompleted();
    }
}