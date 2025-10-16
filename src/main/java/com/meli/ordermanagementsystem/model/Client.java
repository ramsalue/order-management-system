package com.meli.ordermanagementsystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Client entity representing customers in the system
 * This class maps to the 'clients' table in the database
 */
@Entity
@Table(name = "clients")
public class Client {

    /**
     * Primary key for the client entity
     * Auto-generated using IDENTITY strategy
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_client")
    private Long idClient;

    /**
     * Client's full name
     * Cannot be null or blank
     */
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /**
     * Client's address
     * Cannot be null or blank
     */
    @NotBlank(message = "Address is required")
    @Size(max = 255, message = "Address cannot exceed 255 characters")
    @Column(name = "address", nullable = false)
    private String address;

    /**
     * Client's age
     * Must be between 18 and 120
     */
    @NotNull(message = "Age is required")
    @Min(value = 18, message = "Age must be at least 18")
    @Max(value = 120, message = "Age must be less than 120")
    @Column(name = "age", nullable = false)
    private Integer age;

    /**
     * List of orders associated with this client
     * One-to-Many relationship with Order entity
     * Cascade ALL means operations on client affect orders
     * orphanRemoval true means removing order from list deletes it
     */
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    /**
     * Default constructor required by JPA
     */
    public Client() {
    }

    /**
     * Constructor with all fields except ID and orders
     * @param name client's name
     * @param address client's address
     * @param age client's age
     */
    public Client(String name, String address, Integer age) {
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

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    /**
     * Utility method to add an order to this client
     * Maintains bidirectional relationship
     * @param order the order to add
     */
    public void addOrder(Order order) {
        orders.add(order);
        order.setClient(this);
    }

    /**
     * Utility method to remove an order from this client
     * Maintains bidirectional relationship
     * @param order the order to remove
     */
    public void removeOrder(Order order) {
        orders.remove(order);
        order.setClient(null);
    }

    @Override
    public String toString() {
        return "Client{" +
                "idClient=" + idClient +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", age=" + age +
                '}';
    }
}