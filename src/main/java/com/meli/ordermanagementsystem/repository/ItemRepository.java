package com.meli.ordermanagementsystem.repository;

import com.meli.ordermanagementsystem.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Item entity
 * Provides data access operations for Item management
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    /**
     * Finds an item by exact name match
     * @param name the item's name
     * @return Optional containing the item if found
     */
    Optional<Item> findByName(String name);

    /**
     * Finds items whose names contain the given string (case-insensitive)
     * @param name partial name to search
     * @return List of items matching the search criteria
     */
    List<Item> findByNameContainingIgnoreCase(String name);

    /**
     * Finds items by exact price
     * @param price the item's price
     * @return List of items with the specified price
     */
    List<Item> findByPrice(BigDecimal price);

    /**
     * Finds items with price less than or equal to specified value
     * @param price maximum price
     * @return List of items within budget
     */
    List<Item> findByPriceLessThanEqual(BigDecimal price);

    /**
     * Finds items with price greater than or equal to specified value
     * @param price minimum price
     * @return List of items above price threshold
     */
    List<Item> findByPriceGreaterThanEqual(BigDecimal price);

    /**
     * Finds items within a price range
     * @param minPrice minimum price (inclusive)
     * @param maxPrice maximum price (inclusive)
     * @return List of items within the price range
     */
    List<Item> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    /**
     * Finds items by description containing specific text
     * @param description partial description to search
     * @return List of items matching the description criteria
     */
    List<Item> findByDescriptionContainingIgnoreCase(String description);

    /**
     * Checks if an item exists with the given name
     * @param name the item's name
     * @return true if item exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Custom query to find all items ordered by price ascending
     * @return List of items sorted by price from lowest to highest
     */
    @Query("SELECT i FROM Item i ORDER BY i.price ASC")
    List<Item> findAllOrderByPriceAsc();

    /**
     * Custom query to find all items ordered by price descending
     * @return List of items sorted by price from highest to lowest
     */
    @Query("SELECT i FROM Item i ORDER BY i.price DESC")
    List<Item> findAllOrderByPriceDesc();

    /**
     * Custom query to find items that have been ordered
     * @return List of items that appear in at least one order
     */
    @Query("SELECT DISTINCT i FROM Item i JOIN i.orders o")
    List<Item> findItemsInOrders();

    /**
     * Custom query to find items that have never been ordered
     * @return List of items not present in any order
     */
    @Query("SELECT i FROM Item i WHERE i.orders IS EMPTY")
    List<Item> findItemsNeverOrdered();

    /**
     * Custom query to count how many times an item has been ordered
     * @param itemId the item's ID
     * @return number of orders containing this item
     */
    @Query("SELECT COUNT(o) FROM Order o JOIN o.items i WHERE i.itemId = :itemId")
    Long countOrdersContainingItem(@Param("itemId") Long itemId);

    /**
     * Custom query to find top N most expensive items
     * @param limit number of items to return
     * @return List of most expensive items
     */
    @Query(value = "SELECT * FROM items ORDER BY price DESC LIMIT :limit", nativeQuery = true)
    List<Item> findTopMostExpensiveItems(@Param("limit") int limit);
}