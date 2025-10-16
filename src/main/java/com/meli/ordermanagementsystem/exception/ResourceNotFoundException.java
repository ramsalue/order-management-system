package com.meli.ordermanagementsystem.exception;

/**
 * Exception thrown when a requested resource is not found
 * Used for 404 Not Found scenarios
 */
public class ResourceNotFoundException extends RuntimeException {

    private String resourceName;
    private String fieldName;
    private Object fieldValue;

    /**
     * Constructor with message only
     * @param message error message
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructor with resource details
     * @param resourceName name of the resource (e.g., "Client", "Order")
     * @param fieldName name of the field used for search (e.g., "id", "name")
     * @param fieldValue value that was not found
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getFieldValue() {
        return fieldValue;
    }
}