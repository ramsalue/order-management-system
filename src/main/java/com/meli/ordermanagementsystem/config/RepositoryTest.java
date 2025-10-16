package com.meli.ordermanagementsystem.config;

import com.meli.ordermanagementsystem.model.Client;
import com.meli.ordermanagementsystem.model.Item;
import com.meli.ordermanagementsystem.repository.ClientRepository;
import com.meli.ordermanagementsystem.repository.ItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Repository test component
 * Tests basic repository operations at startup
 * Remove or comment out after verification
 */
//@Component
public class RepositoryTest implements CommandLineRunner {

    private final ClientRepository clientRepository;
    private final ItemRepository itemRepository;

    public RepositoryTest(ClientRepository clientRepository, ItemRepository itemRepository) {
        this.clientRepository = clientRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("========================================");
        System.out.println("REPOSITORY TEST");
        System.out.println("========================================");

        // Test Client Repository
        Client testClient = new Client("John Doe", "123 Main St", 30);
        Client savedClient = clientRepository.save(testClient);
        System.out.println("Client saved: " + savedClient);

        // Test Item Repository
        Item testItem = new Item("Laptop", "High-performance laptop", new BigDecimal("999.99"));
        Item savedItem = itemRepository.save(testItem);
        System.out.println("Item saved: " + savedItem);

        // Query test
        System.out.println("Total clients: " + clientRepository.count());
        System.out.println("Total items: " + itemRepository.count());

        System.out.println("========================================");
    }
}