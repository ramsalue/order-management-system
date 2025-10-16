package com.meli.ordermanagementsystem.exception;

/**
 * Exception thrown when a business rule is violated
 * Used for business logic validation failures
 */
public class BusinessException extends RuntimeException {

    /**
     * Constructor with message
     * @param message error message describing the business rule violation
     */
    public BusinessException(String message) {
        super(message);
    }

    /**
     * Constructor with message and cause
     * @param message error message
     * @param cause underlying cause
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}