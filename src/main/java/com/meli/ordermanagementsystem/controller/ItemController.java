package com.meli.ordermanagementsystem.controller;

import com.meli.ordermanagementsystem.dto.ItemDTO;
import com.meli.ordermanagementsystem.model.Item;
import com.meli.ordermanagementsystem.service.ItemService;
import com.meli.ordermanagementsystem.util.EntityMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Items", description = "Item catalog operations")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @Operation(
            summary = "Create a new item",
            description = "Creates a new item in the product catalog. The item name must be unique and price must be greater than zero."
    ) //
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Item created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ItemDTO.class)
                    )
            ), //
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data or item name already exists",
                    content = @Content(mediaType = "application/json")
            ) //
    })
    @PostMapping
    public ResponseEntity<ItemDTO> createItem(
            @Parameter(description = "Item information to create", required = true) //
            @Valid @RequestBody ItemDTO itemDTO) {
        Item item = EntityMapper.toItemEntity(itemDTO);
        Item savedItem = itemService.createItem(item);
        ItemDTO responseDTO = EntityMapper.toItemDTO(savedItem);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get item by ID",
            description = "Retrieves a specific item by its unique identifier"
    ) //
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Item found and returned successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ItemDTO.class)
                    )
            ), //
            @ApiResponse(
                    responseCode = "404",
                    description = "Item not found with the provided ID",
                    content = @Content(mediaType = "application/json")
            ) //
    })
    @GetMapping("/{id}")
    public ResponseEntity<ItemDTO> getItemById(
            @Parameter(description = "ID of the item to retrieve", required = true, example = "1") //
            @PathVariable Long id) {
        Item item = itemService.getItemById(id);
        ItemDTO itemDTO = EntityMapper.toItemDTO(item);
        return ResponseEntity.ok(itemDTO);
    }

    @Operation(
            summary = "Get all items",
            description = "Retrieves a list of all items in the product catalog"
    ) //
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of items returned successfully (may be empty)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ItemDTO.class)
                    )
            ) //
    })
    @GetMapping
    public ResponseEntity<List<ItemDTO>> getAllItems() {
        List<Item> items = itemService.getAllItems();
        List<ItemDTO> itemDTOs = items.stream()
                .map(EntityMapper::toItemDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(itemDTOs);
    }

    @Operation(
            summary = "Search items by name",
            description = "Searches for items whose names contain the provided search term (case-insensitive)"
    ) //
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Search completed successfully, returns matching items",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ItemDTO.class)
                    )
            ) //
    })
    @GetMapping("/search")
    public ResponseEntity<List<ItemDTO>> searchItemsByName(
            @Parameter(description = "Search term for item name", required = true, example = "Mouse") //
            @RequestParam String name) {
        List<Item> items = itemService.searchItemsByName(name);
        List<ItemDTO> itemDTOs = items.stream()
                .map(EntityMapper::toItemDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(itemDTOs);
    }

    @Operation(
            summary = "Get items by price range",
            description = "Retrieves items whose prices fall within the specified range (inclusive)"
    ) //
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Items within price range returned successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ItemDTO.class)
                    )
            ), //
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid price range (minPrice greater than maxPrice)",
                    content = @Content(mediaType = "application/json")
            ) //
    })
    @GetMapping("/price-range")
    public ResponseEntity<List<ItemDTO>> getItemsByPriceRange(
            @Parameter(description = "Minimum price (inclusive)", required = true, example = "20.00") //
            @RequestParam BigDecimal minPrice,
            @Parameter(description = "Maximum price (inclusive)", required = true, example = "50.00") //
            @RequestParam BigDecimal maxPrice) {
        List<Item> items = itemService.getItemsByPriceRange(minPrice, maxPrice);
        List<ItemDTO> itemDTOs = items.stream()
                .map(EntityMapper::toItemDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(itemDTOs);
    }

    @Operation(
            summary = "Get items within budget",
            description = "Retrieves all items with price less than or equal to the specified maximum price"
    ) //
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Items within budget returned successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ItemDTO.class)
                    )
            ), //
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid maximum price (must be greater than zero)",
                    content = @Content(mediaType = "application/json")
            ) //
    })
    @GetMapping("/within-budget")
    public ResponseEntity<List<ItemDTO>> getItemsWithinBudget(
            @Parameter(description = "Maximum price budget", required = true, example = "100.00") //
            @RequestParam BigDecimal maxPrice) {
        List<Item> items = itemService.getItemsWithinBudget(maxPrice);
        List<ItemDTO> itemDTOs = items.stream()
                .map(EntityMapper::toItemDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(itemDTOs);
    }

    @Operation(
            summary = "Get items sorted by price ascending",
            description = "Retrieves all items sorted by price from lowest to highest"
    ) //
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Items sorted by price ascending returned successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ItemDTO.class)
                    )
            ) //
    })
    @GetMapping("/sorted/price-asc")
    public ResponseEntity<List<ItemDTO>> getItemsSortedByPriceAsc() {
        List<Item> items = itemService.getItemsSortedByPriceAsc();
        List<ItemDTO> itemDTOs = items.stream()
                .map(EntityMapper::toItemDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(itemDTOs);
    }

    @Operation(
            summary = "Get items sorted by price descending",
            description = "Retrieves all items sorted by price from highest to lowest"
    ) //
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Items sorted by price descending returned successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ItemDTO.class)
                    )
            ) //
    })
    @GetMapping("/sorted/price-desc")
    public ResponseEntity<List<ItemDTO>> getItemsSortedByPriceDesc() {
        List<Item> items = itemService.getItemsSortedByPriceDesc();
        List<ItemDTO> itemDTOs = items.stream()
                .map(EntityMapper::toItemDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(itemDTOs);
    }

    @Operation(
            summary = "Update an existing item",
            description = "Updates the information of an existing item. The new name must be unique if changed."
    ) //
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Item updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ItemDTO.class)
                    )
            ), //
            @ApiResponse(
                    responseCode = "404",
                    description = "Item not found with the provided ID",
                    content = @Content(mediaType = "application/json")
            ), //
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data or duplicate item name",
                    content = @Content(mediaType = "application/json")
            ) //
    })
    @PutMapping("/{id}")
    public ResponseEntity<ItemDTO> updateItem(
            @Parameter(description = "ID of the item to update", required = true, example = "1") //
            @PathVariable Long id,
            @Parameter(description = "Updated item information", required = true) //
            @Valid @RequestBody ItemDTO itemDTO) {
        Item itemDetails = EntityMapper.toItemEntity(itemDTO);
        Item updatedItem = itemService.updateItem(id, itemDetails);
        ItemDTO responseDTO = EntityMapper.toItemDTO(updatedItem);
        return ResponseEntity.ok(responseDTO);
    }

    @Operation(
            summary = "Delete an item",
            description = "Deletes an item from the catalog. Cannot delete items that exist in orders."
    ) //
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Item deleted successfully"
            ), //
            @ApiResponse(
                    responseCode = "404",
                    description = "Item not found with the provided ID",
                    content = @Content(mediaType = "application/json")
            ), //
            @ApiResponse(
                    responseCode = "400",
                    description = "Cannot delete item that exists in orders",
                    content = @Content(mediaType = "application/json")
            ) //
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(
            @Parameter(description = "ID of the item to delete", required = true, example = "1") //
            @PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get items in orders",
            description = "Retrieves all items that appear in at least one order"
    ) //
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of items in orders returned successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ItemDTO.class)
                    )
            ) //
    })
    @GetMapping("/in-orders")
    public ResponseEntity<List<ItemDTO>> getItemsInOrders() {
        List<Item> items = itemService.getItemsInOrders();
        List<ItemDTO> itemDTOs = items.stream()
                .map(EntityMapper::toItemDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(itemDTOs);
    }

    @Operation(
            summary = "Get items never ordered",
            description = "Retrieves all items that have never been included in any order"
    ) //
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of items never ordered returned successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ItemDTO.class)
                    )
            ) //
    })
    @GetMapping("/never-ordered")
    public ResponseEntity<List<ItemDTO>> getItemsNeverOrdered() {
        List<Item> items = itemService.getItemsNeverOrdered();
        List<ItemDTO> itemDTOs = items.stream()
                .map(EntityMapper::toItemDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(itemDTOs);
    }

    @Operation(
            summary = "Get item order count",
            description = "Returns the total number of orders that include a specific item"
    ) //
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Order count returned successfully",
                    content = @Content(mediaType = "application/json")
            ), //
            @ApiResponse(
                    responseCode = "404",
                    description = "Item not found with the provided ID",
                    content = @Content(mediaType = "application/json")
            ) //
    })
    @GetMapping("/{id}/order-count")
    public ResponseEntity<Long> getItemOrderCount(
            @Parameter(description = "ID of the item", required = true, example = "1") //
            @PathVariable Long id) {
        Long count = itemService.getItemOrderCount(id);
        return ResponseEntity.ok(count);
    }

    @Operation(
            summary = "Get top expensive items",
            description = "Retrieves the top N most expensive items from the catalog"
    ) //
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Top expensive items returned successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ItemDTO.class)
                    )
            ), //
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid limit (must be greater than zero)",
                    content = @Content(mediaType = "application/json")
            ) //
    })
    @GetMapping("/top-expensive")
    public ResponseEntity<List<ItemDTO>> getTopMostExpensiveItems(
            @Parameter(description = "Number of items to return", required = true, example = "5") //
            @RequestParam int limit) {
        List<Item> items = itemService.getTopMostExpensiveItems(limit);
        List<ItemDTO> itemDTOs = items.stream()
                .map(EntityMapper::toItemDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(itemDTOs);
    }
}