package com.meli.ordermanagementsystem.service;

import com.meli.ordermanagementsystem.exception.BusinessException;
import com.meli.ordermanagementsystem.exception.ResourceNotFoundException;
import com.meli.ordermanagementsystem.model.Item;
import com.meli.ordermanagementsystem.repository.ItemRepository;
import com.meli.ordermanagementsystem.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ItemService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ItemService Unit Tests")
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    private Item testItem;

    @BeforeEach
    void setUp() {
        testItem = new Item("Test Item", "Test Description", new BigDecimal("99.99"));
        testItem.setItemId(1L);
    }

    // ========================================
    // CREATE ITEM TESTS
    // ========================================

    @Test
    @DisplayName("Should create item successfully")
    void testCreateItem_Success() {
        // Given
        when(itemRepository.existsByName(testItem.getName())).thenReturn(false);
        when(itemRepository.save(any(Item.class))).thenReturn(testItem);

        // When
        Item createdItem = itemService.createItem(testItem);

        // Then
        assertThat(createdItem).isNotNull();
        assertThat(createdItem.getName()).isEqualTo("Test Item");
        assertThat(createdItem.getPrice()).isEqualByComparingTo(new BigDecimal("99.99"));

        verify(itemRepository, times(1)).existsByName("Test Item");
        verify(itemRepository, times(1)).save(testItem);
    }

    @Test
    @DisplayName("Should throw BusinessException when item name already exists")
    void testCreateItem_DuplicateName() {
        // Given
        when(itemRepository.existsByName(testItem.getName())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> itemService.createItem(testItem))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already exists");

        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when price is zero")
    void testCreateItem_ZeroPrice() {
        // Given
        Item zeroPrice = new Item("Zero Item", "Test", BigDecimal.ZERO);
        when(itemRepository.existsByName(zeroPrice.getName())).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> itemService.createItem(zeroPrice))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("greater than zero");

        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when price is negative")
    void testCreateItem_NegativePrice() {
        // Given
        Item negativePrice = new Item("Negative Item", "Test", new BigDecimal("-10.00"));
        when(itemRepository.existsByName(negativePrice.getName())).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> itemService.createItem(negativePrice))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("greater than zero");

        verify(itemRepository, never()).save(any(Item.class));
    }

    // ========================================
    // GET ITEM TESTS
    // ========================================

    @Test
    @DisplayName("Should get item by ID successfully")
    void testGetItemById_Success() {
        // Given
        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));

        // When
        Item foundItem = itemService.getItemById(1L);

        // Then
        assertThat(foundItem).isNotNull();
        assertThat(foundItem.getItemId()).isEqualTo(1L);
        assertThat(foundItem.getName()).isEqualTo("Test Item");

        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when item not found")
    void testGetItemById_NotFound() {
        // Given
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> itemService.getItemById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("not found");

        verify(itemRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should get all items successfully")
    void testGetAllItems_Success() {
        // Given
        Item item2 = new Item("Item 2", "Description 2", new BigDecimal("49.99"));
        item2.setItemId(2L);
        when(itemRepository.findAll()).thenReturn(Arrays.asList(testItem, item2));

        // When
        List<Item> items = itemService.getAllItems();

        // Then
        assertThat(items).hasSize(2);
        assertThat(items).contains(testItem, item2);

        verify(itemRepository, times(1)).findAll();
    }

    // ========================================
    // SEARCH ITEM TESTS
    // ========================================

    @Test
    @DisplayName("Should search items by name successfully")
    void testSearchItemsByName_Success() {
        // Given
        when(itemRepository.findByNameContainingIgnoreCase("Test"))
                .thenReturn(Arrays.asList(testItem));

        // When
        List<Item> items = itemService.searchItemsByName("Test");

        // Then
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getName()).contains("Test");

        verify(itemRepository, times(1)).findByNameContainingIgnoreCase("Test");
    }

    @Test
    @DisplayName("Should get items by price range successfully")
    void testGetItemsByPriceRange_Success() {
        // Given
        BigDecimal minPrice = new BigDecimal("50.00");
        BigDecimal maxPrice = new BigDecimal("150.00");
        when(itemRepository.findByPriceBetween(minPrice, maxPrice))
                .thenReturn(Arrays.asList(testItem));

        // When
        List<Item> items = itemService.getItemsByPriceRange(minPrice, maxPrice);

        // Then
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getPrice()).isBetween(minPrice, maxPrice);

        verify(itemRepository, times(1)).findByPriceBetween(minPrice, maxPrice);
    }

    @Test
    @DisplayName("Should throw BusinessException when price range is invalid")
    void testGetItemsByPriceRange_InvalidRange() {
        // Given
        BigDecimal minPrice = new BigDecimal("150.00");
        BigDecimal maxPrice = new BigDecimal("50.00");

        // When & Then
        assertThatThrownBy(() -> itemService.getItemsByPriceRange(minPrice, maxPrice))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("cannot be greater than");

        verify(itemRepository, never()).findByPriceBetween(any(), any());
    }

    // ========================================
    // UPDATE ITEM TESTS
    // ========================================

    @Test
    @DisplayName("Should update item successfully")
    void testUpdateItem_Success() {
        // Given
        Item updatedData = new Item("Updated Item", "New Description", new BigDecimal("149.99"));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));
        when(itemRepository.existsByName("Updated Item")).thenReturn(false);
        when(itemRepository.save(any(Item.class))).thenReturn(testItem);

        // When
        Item updatedItem = itemService.updateItem(1L, updatedData);

        // Then
        assertThat(updatedItem).isNotNull();
        verify(itemRepository, times(1)).findById(1L);
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when updating to duplicate name")
    void testUpdateItem_DuplicateName() {
        // Given
        Item updatedData = new Item("Another Item", "Description", new BigDecimal("99.99"));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));
        when(itemRepository.existsByName("Another Item")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> itemService.updateItem(1L, updatedData))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already exists");

        verify(itemRepository, never()).save(any(Item.class));
    }

    // ========================================
    // DELETE ITEM TESTS
    // ========================================

    @Test
    @DisplayName("Should delete item successfully when not in orders")
    void testDeleteItem_Success() {
        // Given
        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));
        when(itemRepository.countOrdersContainingItem(1L)).thenReturn(0L);
        doNothing().when(itemRepository).delete(testItem);

        // When
        itemService.deleteItem(1L);

        // Then
        verify(itemRepository, times(1)).findById(1L);
        verify(itemRepository, times(1)).countOrdersContainingItem(1L);
        verify(itemRepository, times(1)).delete(testItem);
    }

    @Test
    @DisplayName("Should throw BusinessException when deleting item in orders")
    void testDeleteItem_InOrders() {
        // Given
        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));
        when(itemRepository.countOrdersContainingItem(1L)).thenReturn(3L);

        // When & Then
        assertThatThrownBy(() -> itemService.deleteItem(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("exists in orders");

        verify(itemRepository, never()).delete(any(Item.class));
    }

    // ========================================
    // BUSINESS LOGIC TESTS
    // ========================================

    @Test
    @DisplayName("Should get items sorted by price ascending")
    void testGetItemsSortedByPriceAsc_Success() {
        // Given
        when(itemRepository.findAllOrderByPriceAsc()).thenReturn(Arrays.asList(testItem));

        // When
        List<Item> items = itemService.getItemsSortedByPriceAsc();

        // Then
        assertThat(items).hasSize(1);
        verify(itemRepository, times(1)).findAllOrderByPriceAsc();
    }

    @Test
    @DisplayName("Should get items within budget")
    void testGetItemsWithinBudget_Success() {
        // Given
        BigDecimal budget = new BigDecimal("100.00");
        when(itemRepository.findByPriceLessThanEqual(budget)).thenReturn(Arrays.asList(testItem));

        // When
        List<Item> items = itemService.getItemsWithinBudget(budget);

        // Then
        assertThat(items).hasSize(1);
        verify(itemRepository, times(1)).findByPriceLessThanEqual(budget);
    }

    @Test
    @DisplayName("Should get top expensive items")
    void testGetTopMostExpensiveItems_Success() {
        // Given
        when(itemRepository.findTopMostExpensiveItems(5)).thenReturn(Arrays.asList(testItem));

        // When
        List<Item> items = itemService.getTopMostExpensiveItems(5);

        // Then
        assertThat(items).hasSize(1);
        verify(itemRepository, times(1)).findTopMostExpensiveItems(5);
    }

    @Test
    @DisplayName("Should throw BusinessException when limit is zero or negative")
    void testGetTopMostExpensiveItems_InvalidLimit() {
        // When & Then
        assertThatThrownBy(() -> itemService.getTopMostExpensiveItems(0))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("greater than 0");

        assertThatThrownBy(() -> itemService.getTopMostExpensiveItems(-1))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("greater than 0");

        verify(itemRepository, never()).findTopMostExpensiveItems(anyInt());
    }
}