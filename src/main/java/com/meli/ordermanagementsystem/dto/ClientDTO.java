package com.meli.ordermanagementsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

/**
 * Data Transfer Object for Client
 * Used for API requests and responses
 */
@Schema(description = "Client information for customer management")
public class ClientDTO {

    @Schema(description = "Unique identifier of the client",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Long idClient;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Schema(description = "Full name of the client",
            example = "John Smith",
            required = true,
            minLength = 2,
            maxLength = 100)
    private String name;

    @NotBlank(message = "Address is required")
    @Size(max = 255, message = "Address cannot exceed 255 characters")
    @Schema(description = "Physical address of the client",
            example = "123 Main Street, New York, NY 10001",
            required = true,
            maxLength = 255)
    private String address;

    @NotNull(message = "Age is required")
    @Min(value = 18, message = "Age must be at least 18")
    @Max(value = 120, message = "Age must be less than 120")
    @Schema(description = "Age of the client (must be 18 or older)",
            example = "30",
            required = true,
            minimum = "18",
            maximum = "120")
    private Integer age;

    /**
     * Default constructor
     */
    public ClientDTO() {
    }

    /**
     * Constructor with all fields
     */
    public ClientDTO(Long idClient, String name, String address, Integer age) {
        this.idClient = idClient;
        this.name = name;
        this.address = address;
        this.age = age;
    }

    // Getters and Setters

    public Long getIdClient() {
        return idClient;
    }

    public void setIdClient(Long idClient) {
        this.idClient = idClient;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}