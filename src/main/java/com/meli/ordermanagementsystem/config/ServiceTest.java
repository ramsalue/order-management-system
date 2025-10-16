package com.meli.ordermanagementsystem.config;

import com.meli.ordermanagementsystem.model.Client;
import com.meli.ordermanagementsystem.model.Item;
import com.meli.ordermanagementsystem.model.Order;
import com.meli.ordermanagementsystem.model.OrderStatus;
import com.meli.ordermanagementsystem.service.ClientService;
import com.meli.ordermanagementsystem.service.ItemService;
import com.meli.ordermanagementsystem.service.OrderService;
import org.springframework.boot.CommandLineRunner;
//import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

/**
 * Service layer test component
 * Tests service operations at startup
 * Remove or comment out after verification
 */
//@Component
//@Order(2) // Run after DatabaseConnectionTest
public class ServiceTest implements CommandLineRunner {

    private final ClientService clientService;
    private final ItemService itemService;
    private final OrderService orderService;

    public ServiceTest(ClientService clientService,
                       ItemService itemService,
                       OrderService orderService) {
        this.clientService = clientService;
        this.itemService = itemService;
        this.orderService = orderService;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("========================================");
        System.out.println("SERVICE LAYER TEST");
        System.out.println("========================================");

        try {
            // Test Client Service
            Client client = new Client("Jane Smith", "456 Oak Avenue", 28);
            Client savedClient = clientService.createClient(client);
            System.out.println("Client created via service: " + savedClient);

            // Test Item Service
            Item item1 = new Item("Smartphone", "Latest model smartphone", new BigDecimal("799.99"));
            Item savedItem1 = itemService.createItem(item1);
            System.out.println("Item 1 created via service: " + savedItem1);

            Item item2 = new Item("Headphones", "Wireless noise-cancelling", new BigDecimal("199.99"));
            Item savedItem2 = itemService.createItem(item2);
            System.out.println("Item 2 created via service: " + savedItem2);

            // Test Order Service
            com.meli.ordermanagementsystem.model.Order order = new com.meli.ordermanagementsystem.model.Order();
            order.setPurchaseDate(LocalDate.now());
            order.setDeliveryDate(LocalDate.now().plusDays(5));

            Set<Long> itemIds = Set.of(savedItem1.getItemId(), savedItem2.getItemId());
            com.meli.ordermanagementsystem.model.Order savedOrder = orderService.createOrder(
                    order,
                    savedClient.getIdClient(),
                    itemIds
            );
            System.out.println("Order created via service: " + savedOrder);
            System.out.println("Order contains " + savedOrder.getItems().size() + " items");

            // Test status update
            com.meli.ordermanagementsystem.model.Order updatedOrder = orderService.updateOrderStatus(
                    savedOrder.getIdOrder(),
                    OrderStatus.PROCESSING
            );
            System.out.println("Order status updated to: " + updatedOrder.getStatus());

            // Test query methods
            System.out.println("Total clients: " + clientService.getAllClients().size());
            System.out.println("Total items: " + itemService.getAllItems().size());
            System.out.println("Total orders: " + orderService.getAllOrders().size());
            System.out.println("Pending orders: " + orderService.countOrdersByStatus(OrderStatus.PENDING));
            System.out.println("Processing orders: " + orderService.countOrdersByStatus(OrderStatus.PROCESSING));

            System.out.println("========================================");
            System.out.println("SERVICE TEST COMPLETED SUCCESSFULLY");
            System.out.println("========================================");

        } catch (Exception e) {
            System.err.println("========================================");
            System.err.println("SERVICE TEST FAILED");
            System.err.println("Error: " + e.getMessage());
            System.err.println("========================================");
            e.printStackTrace();
        }
    }
}