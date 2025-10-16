package com.meli.ordermanagementsystem.service;

import com.meli.ordermanagementsystem.exception.BusinessException;
import com.meli.ordermanagementsystem.exception.ResourceNotFoundException;
import com.meli.ordermanagementsystem.model.Item;
import com.meli.ordermanagementsystem.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service class for Item business logic
 * Handles all item-related operations and validations
 */
@Service
@Transactional
public class ItemService {

    private final ItemRepository itemRepository;

    /**
     * Constructor with dependency injection
     * @param itemRepository the item repository
     */
    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    /**
     * Creates a new item
     * Validates that item name is unique and price is valid
     * @param item the item to create
     * @return the created item with generated ID
     * @throws BusinessException if validation fails
     */
    public Item createItem(Item item) {
        // Validate unique name
        if (itemRepository.existsByName(item.getName())) {
            throw new BusinessException("Item with name '" + item.getName() + "' already exists");
        }

        // Validate price
        validatePrice(item.getPrice());

        return itemRepository.save(item);
    }

    /**
     * Retrieves an item by ID
     * @param id the item ID
     * @return the item
     * @throws ResourceNotFoundException if item not found
     */
    @Transactional(readOnly = true)
    public Item getItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item", "id", id));
    }

    /**
     * Retrieves all items
     * @return list of all items
     */
    @Transactional(readOnly = true)
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    /**
     * Searches items by name (partial match)
     * @param name partial name to search
     * @return list of matching items
     */
    @Transactional(readOnly = true)
    public List<Item> searchItemsByName(String name) {
        return itemRepository.findByNameContainingIgnoreCase(name);
    }

    /**
     * Gets items within a price range
     * @param minPrice minimum price
     * @param maxPrice maximum price
     * @return list of items in price range
     */
    @Transactional(readOnly = true)
    public List<Item> getItemsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        validatePriceRange(minPrice, maxPrice);
        return itemRepository.findByPriceBetween(minPrice, maxPrice);
    }

    /**
     * Gets items sorted by price ascending
     * @return list of items from cheapest to most expensive
     */
    @Transactional(readOnly = true)
    public List<Item> getItemsSortedByPriceAsc() {
        return itemRepository.findAllOrderByPriceAsc();
    }

    /**
     * Gets items sorted by price descending
     * @return list of items from most expensive to cheapest
     */
    @Transactional(readOnly = true)
    public List<Item> getItemsSortedByPriceDesc() {
        return itemRepository.findAllOrderByPriceDesc();
    }

    /**
     * Gets items within budget (price less than or equal to max)
     * @param maxPrice maximum price budget
     * @return list of items within budget
     */
    @Transactional(readOnly = true)
    public List<Item> getItemsWithinBudget(BigDecimal maxPrice) {
        validatePrice(maxPrice);
        return itemRepository.findByPriceLessThanEqual(maxPrice);
    }

    /**
     * Updates an existing item
     * @param id the item ID
     * @param itemDetails the updated item data
     * @return the updated item
     * @throws ResourceNotFoundException if item not found
     */
    public Item updateItem(Long id, Item itemDetails) {
        Item item = getItemById(id);

        // Check if name is being changed and if new name already exists
        if (!item.getName().equals(itemDetails.getName()) &&
                itemRepository.existsByName(itemDetails.getName())) {
            throw new BusinessException("Item with name '" + itemDetails.getName() + "' already exists");
        }

        // Validate price
        validatePrice(itemDetails.getPrice());

        // Update fields
        item.setName(itemDetails.getName());
        item.setDescription(itemDetails.getDescription());
        item.setPrice(itemDetails.getPrice());

        return itemRepository.save(item);
    }

    /**
     * Deletes an item by ID
     * Validates that item is not in any orders before deletion
     * @param id the item ID
     * @throws ResourceNotFoundException if item not found
     * @throws BusinessException if item is in orders
     */
    public void deleteItem(Long id) {
        Item item = getItemById(id);

        // Business rule: cannot delete item that is in orders
        Long orderCount = itemRepository.countOrdersContainingItem(id);
        if (orderCount > 0) {
            throw new BusinessException("Cannot delete item that exists in orders. Item is in " + orderCount + " orders.");
        }

        itemRepository.delete(item);
    }

    /**
     * Gets items that have been ordered
     * @return list of items in at least one order
     */
    @Transactional(readOnly = true)
    public List<Item> getItemsInOrders() {
        return itemRepository.findItemsInOrders();
    }

    /**
     * Gets items that have never been ordered
     * @return list of items not in any order
     */
    @Transactional(readOnly = true)
    public List<Item> getItemsNeverOrdered() {
        return itemRepository.findItemsNeverOrdered();
    }

    /**
     * Gets count of orders containing a specific item
     * @param id the item ID
     * @return number of orders containing the item
     */
    @Transactional(readOnly = true)
    public Long getItemOrderCount(Long id) {
        getItemById(id); // Verify item exists
        return itemRepository.countOrdersContainingItem(id);
    }

    /**
     * Gets top N most expensive items
     * @param limit number of items to return
     * @return list of most expensive items
     */
    @Transactional(readOnly = true)
    public List<Item> getTopMostExpensiveItems(int limit) {
        if (limit <= 0) {
            throw new BusinessException("Limit must be greater than 0");
        }
        return itemRepository.findTopMostExpensiveItems(limit);
    }

    /**
     * Private helper method to validate price
     * @param price the price to validate
     * @throws BusinessException if price is invalid
     */
    private void validatePrice(BigDecimal price) {
        if (price == null) {
            throw new BusinessException("Price cannot be null");
        }
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Price must be greater than zero");
        }
        // Optional: set maximum price limit
        BigDecimal maxPrice = new BigDecimal("999999.99");
        if (price.compareTo(maxPrice) > 0) {
            throw new BusinessException("Price cannot exceed " + maxPrice);
        }
    }

    /**
     * Private helper method to validate price range
     * @param minPrice minimum price
     * @param maxPrice maximum price
     * @throws BusinessException if range is invalid
     */
    private void validatePriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        validatePrice(minPrice);
        validatePrice(maxPrice);
        if (minPrice.compareTo(maxPrice) > 0) {
            throw new BusinessException("Minimum price cannot be greater than maximum price");
        }
    }
}