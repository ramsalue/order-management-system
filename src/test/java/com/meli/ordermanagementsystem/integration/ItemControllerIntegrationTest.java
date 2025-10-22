package com.meli.ordermanagementsystem.integration;

import com.meli.ordermanagementsystem.dto.ItemDTO;
import com.meli.ordermanagementsystem.model.Item;
import com.meli.ordermanagementsystem.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Item API endpoints
 */
public class ItemControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    public void setUp() {
        itemRepository.deleteAll();
    }

    /**
     * Test creating an item
     */
    @Test
    public void testCreateItem_Success() throws Exception {
        // Given
        ItemDTO itemDTO = new ItemDTO(
                null,
                "Laptop",
                "High-performance laptop",
                new BigDecimal("999.99")
        );

        // When & Then
        MvcResult result = mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(itemDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.itemId").exists())
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.price").value(999.99))
                .andReturn();

        // Verify in database
        String responseJson = result.getResponse().getContentAsString();
        ItemDTO createdItem = fromJsonString(responseJson, ItemDTO.class);

        Item itemInDb = itemRepository.findById(createdItem.getItemId()).orElse(null);
        assertThat(itemInDb).isNotNull();
        assertThat(itemInDb.getName()).isEqualTo("Laptop");
    }

    /**
     * Test creating item with invalid price
     */
    @Test
    public void testCreateItem_InvalidPrice() throws Exception {
        // Given - negative price
        ItemDTO itemDTO = new ItemDTO(
                null,
                "Invalid Item",
                "Description",
                new BigDecimal("-10.00")
        );

        // When & Then
        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(itemDTO)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test getting item by ID
     */
    @Test
    public void testGetItemById_Success() throws Exception {
        // Given
        Item item = new Item("Mouse", "Wireless mouse", new BigDecimal("29.99"));
        Item savedItem = itemRepository.save(item);

        // When & Then
        mockMvc.perform(get("/api/items/" + savedItem.getItemId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Mouse"))
                .andExpect(jsonPath("$.price").value(29.99));
    }

    /**
     * Test getting all items
     */
    @Test
    public void testGetAllItems_Success() throws Exception {
        // Given
        itemRepository.save(new Item("Item 1", "Desc 1", new BigDecimal("10.00")));
        itemRepository.save(new Item("Item 2", "Desc 2", new BigDecimal("20.00")));

        // When & Then
        mockMvc.perform(get("/api/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    /**
     * Test updating item
     */
    @Test
    public void testUpdateItem_Success() throws Exception {
        // Given
        Item item = new Item("Old Item", "Old Desc", new BigDecimal("50.00"));
        Item savedItem = itemRepository.save(item);

        ItemDTO updateDTO = new ItemDTO(
                savedItem.getItemId(),
                "Updated Item",
                "Updated Description",
                new BigDecimal("55.00")
        );

        // When & Then
        mockMvc.perform(put("/api/items/" + savedItem.getItemId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Item"))
                .andExpect(jsonPath("$.price").value(55.00));
    }

    /**
     * Test deleting item
     */
    @Test
    public void testDeleteItem_Success() throws Exception {
        // Given
        Item item = new Item("Delete Item", "Delete Desc", new BigDecimal("100.00"));
        Item savedItem = itemRepository.save(item);

        // When & Then
        mockMvc.perform(delete("/api/items/" + savedItem.getItemId()))
                .andExpect(status().isNoContent());

        // Verify deleted
        assertThat(itemRepository.findById(savedItem.getItemId())).isEmpty();
    }

    /**
     * Test searching items by name
     */
    @Test
    public void testSearchItemsByName_Success() throws Exception {
        // Given
        itemRepository.save(new Item("Gaming Mouse", "RGB mouse", new BigDecimal("50.00")));
        itemRepository.save(new Item("Gaming Keyboard", "RGB keyboard", new BigDecimal("80.00")));
        itemRepository.save(new Item("Office Chair", "Ergonomic chair", new BigDecimal("200.00")));

        // When & Then
        mockMvc.perform(get("/api/items/search")
                        .param("name", "Gaming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    /**
     * Test getting items by price range
     */
    @Test
    public void testGetItemsByPriceRange_Success() throws Exception {
        // Given
        itemRepository.save(new Item("Cheap Item", "Desc", new BigDecimal("10.00")));
        itemRepository.save(new Item("Medium Item", "Desc", new BigDecimal("50.00")));
        itemRepository.save(new Item("Expensive Item", "Desc", new BigDecimal("500.00")));

        // When & Then
        mockMvc.perform(get("/api/items/price-range")
                        .param("minPrice", "40")
                        .param("maxPrice", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Medium Item"));
    }

    /**
     * Test getting items sorted by price ascending
     */
    @Test
    public void testGetItemsSortedByPriceAsc_Success() throws Exception {
        // Given
        itemRepository.save(new Item("Expensive", "Desc", new BigDecimal("100.00")));
        itemRepository.save(new Item("Cheap", "Desc", new BigDecimal("10.00")));
        itemRepository.save(new Item("Medium", "Desc", new BigDecimal("50.00")));

        // When & Then
        mockMvc.perform(get("/api/items/sorted/price-asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].price").value(10.00))
                .andExpect(jsonPath("$[1].price").value(50.00))
                .andExpect(jsonPath("$[2].price").value(100.00));
    }
}