package com.meli.ordermanagementsystem.controller;

import com.meli.ordermanagementsystem.dto.ItemDTO;
import com.meli.ordermanagementsystem.model.Item;
import com.meli.ordermanagementsystem.service.ItemService;
import com.meli.ordermanagementsystem.util.EntityMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for Item management
 * Handles HTTP requests for item operations
 * Base URL: /api/items
 */
@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    /**
     * Constructor with dependency injection
     * @param itemService the item service
     */
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    /**
     * Creates a new item
     * POST /api/items
     * @param itemDTO item data
     * @return ResponseEntity with created item and 201 status
     */
    @PostMapping
    public ResponseEntity<ItemDTO> createItem(@Valid @RequestBody ItemDTO itemDTO) {
        Item item = EntityMapper.toItemEntity(itemDTO);
        Item savedItem = itemService.createItem(item);
        ItemDTO responseDTO = EntityMapper.toItemDTO(savedItem);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    /**
     * Retrieves an item by ID
     * GET /api/items/{id}
     * @param id the item ID
     * @return ResponseEntity with item data and 200 status
     */
    @GetMapping("/{id}")
    public ResponseEntity<ItemDTO> getItemById(@PathVariable Long id) {
        Item item = itemService.getItemById(id);
        ItemDTO itemDTO = EntityMapper.toItemDTO(item);
        return ResponseEntity.ok(itemDTO);
    }

    /**
     * Retrieves all items
     * GET /api/items
     * @return ResponseEntity with list of items and 200 status
     */
    @GetMapping
    public ResponseEntity<List<ItemDTO>> getAllItems() {
        List<Item> items = itemService.getAllItems();
        List<ItemDTO> itemDTOs = items.stream()
                .map(EntityMapper::toItemDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(itemDTOs);
    }

    /**
     * Searches items by name
     * GET /api/items/search?name={name}
     * @param name partial name to search
     * @return ResponseEntity with matching items and 200 status
     */
    @GetMapping("/search")
    public ResponseEntity<List<ItemDTO>> searchItemsByName(@RequestParam String name) {
        List<Item> items = itemService.searchItemsByName(name);
        List<ItemDTO> itemDTOs = items.stream()
                .map(EntityMapper::toItemDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(itemDTOs);
    }

    /**
     * Gets items by price range
     * GET /api/items/price-range?minPrice={min}&maxPrice={max}
     * @param minPrice minimum price
     * @param maxPrice maximum price
     * @return ResponseEntity with matching items and 200 status
     */
    @GetMapping("/price-range")
    public ResponseEntity<List<ItemDTO>> getItemsByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {
        List<Item> items = itemService.getItemsByPriceRange(minPrice, maxPrice);
        List<ItemDTO> itemDTOs = items.stream()
                .map(EntityMapper::toItemDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(itemDTOs);
    }

    /**
     * Gets items within budget
     * GET /api/items/within-budget?maxPrice={max}
     * @param maxPrice maximum price
     * @return ResponseEntity with items within budget and 200 status
     */
    @GetMapping("/within-budget")
    public ResponseEntity<List<ItemDTO>> getItemsWithinBudget(@RequestParam BigDecimal maxPrice) {
        List<Item> items = itemService.getItemsWithinBudget(maxPrice);
        List<ItemDTO> itemDTOs = items.stream()
                .map(EntityMapper::toItemDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(itemDTOs);
    }

    /**
     * Gets items sorted by price ascending
     * GET /api/items/sorted/price-asc
     * @return ResponseEntity with sorted items and 200 status
     */
    @GetMapping("/sorted/price-asc")
    public ResponseEntity<List<ItemDTO>> getItemsSortedByPriceAsc() {
        List<Item> items = itemService.getItemsSortedByPriceAsc();
        List<ItemDTO> itemDTOs = items.stream()
                .map(EntityMapper::toItemDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(itemDTOs);
    }

    /**
     * Gets items sorted by price descending
     * GET /api/items/sorted/price-desc
     * @return ResponseEntity with sorted items and 200 status
     */
    @GetMapping("/sorted/price-desc")
    public ResponseEntity<List<ItemDTO>> getItemsSortedByPriceDesc() {
        List<Item> items = itemService.getItemsSortedByPriceDesc();
        List<ItemDTO> itemDTOs = items.stream()
                .map(EntityMapper::toItemDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(itemDTOs);
    }

    /**
     * Updates an existing item
     * PUT /api/items/{id}
     * @param id the item ID
     * @param itemDTO updated item data
     * @return ResponseEntity with updated item and 200 status
     */
    @PutMapping("/{id}")
    public ResponseEntity<ItemDTO> updateItem(
            @PathVariable Long id,
            @Valid @RequestBody ItemDTO itemDTO) {
        Item itemDetails = EntityMapper.toItemEntity(itemDTO);
        Item updatedItem = itemService.updateItem(id, itemDetails);
        ItemDTO responseDTO = EntityMapper.toItemDTO(updatedItem);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Deletes an item by ID
     * DELETE /api/items/{id}
     * @param id the item ID
     * @return ResponseEntity with 204 No Content status
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Gets items that have been ordered
     * GET /api/items/in-orders
     * @return ResponseEntity with items and 200 status
     */
    @GetMapping("/in-orders")
    public ResponseEntity<List<ItemDTO>> getItemsInOrders() {
        List<Item> items = itemService.getItemsInOrders();
        List<ItemDTO> itemDTOs = items.stream()
                .map(EntityMapper::toItemDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(itemDTOs);
    }

    /**
     * Gets items never ordered
     * GET /api/items/never-ordered
     * @return ResponseEntity with items and 200 status
     */
    @GetMapping("/never-ordered")
    public ResponseEntity<List<ItemDTO>> getItemsNeverOrdered() {
        List<Item> items = itemService.getItemsNeverOrdered();
        List<ItemDTO> itemDTOs = items.stream()
                .map(EntityMapper::toItemDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(itemDTOs);
    }

    /**
     * Gets order count for an item
     * GET /api/items/{id}/order-count
     * @param id the item ID
     * @return ResponseEntity with order count and 200 status
     */
    @GetMapping("/{id}/order-count")
    public ResponseEntity<Long> getItemOrderCount(@PathVariable Long id) {
        Long count = itemService.getItemOrderCount(id);
        return ResponseEntity.ok(count);
    }

/**
 * Gets top N most expensive items
 * GET /api/items/top-expensive?limit={n}
 * @param limit number of items to return
 * @return Response
 **/
    @GetMapping("/top-expensive")
    public ResponseEntity<List<ItemDTO>> getTopMostExpensiveItems(@RequestParam int limit) {
        List<Item> items = itemService.getTopMostExpensiveItems(limit);
        List<ItemDTO> itemDTOs = items.stream()
                .map(EntityMapper::toItemDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(itemDTOs);
    }
}