package com.meli.ordermanagementsystem.service;

import com.meli.ordermanagementsystem.exception.BusinessException;
import com.meli.ordermanagementsystem.exception.ResourceNotFoundException;
import com.meli.ordermanagementsystem.model.Client;
import com.meli.ordermanagementsystem.model.Item;
import com.meli.ordermanagementsystem.model.Order;
import com.meli.ordermanagementsystem.model.OrderStatus;
import com.meli.ordermanagementsystem.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OrderService
 * Tests complex business logic with multiple dependencies
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService Unit Tests")
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ClientService clientService;

    @Mock
    private ItemService itemService;

    @InjectMocks
    private OrderService orderService;

    private Order testOrder;
    private Client testClient;
    private Item testItem;

    @BeforeEach
    void setUp() {
        // Set up test client
        testClient = new Client("John Doe", "123 Main St", 30);
        testClient.setIdClient(1L);

        // Set up test item
        testItem = new Item("Test Item", "Test Description", new BigDecimal("99.99"));
        testItem.setItemId(1L);

        // Set up test order
        testOrder = new Order();
        testOrder.setIdOrder(1L);
        testOrder.setClient(testClient);
        testOrder.setPurchaseDate(LocalDate.now());
        testOrder.setDeliveryDate(LocalDate.now().plusDays(7));
        testOrder.setStatus(OrderStatus.PENDING);
        testOrder.addItem(testItem);
    }

    // ========================================
    // CREATE ORDER TESTS
    // ========================================

    @Test
    @DisplayName("Should create order successfully")
    void testCreateOrder_Success() {
        // Given
        Order newOrder = new Order();
        newOrder.setPurchaseDate(LocalDate.now());
        newOrder.setDeliveryDate(LocalDate.now().plusDays(5));

        Set<Long> itemIds = new HashSet<>(Arrays.asList(1L));

        when(clientService.getClientById(1L)).thenReturn(testClient);
        when(itemService.getItemById(1L)).thenReturn(testItem);
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // When
        Order createdOrder = orderService.createOrder(newOrder, 1L, itemIds);

        // Then
        assertThat(createdOrder).isNotNull();
        assertThat(createdOrder.getClient()).isEqualTo(testClient);
        assertThat(createdOrder.getItems()).hasSize(1);
        assertThat(createdOrder.getStatus()).isEqualTo(OrderStatus.PENDING);

        verify(clientService, times(1)).getClientById(1L);
        verify(itemService, times(1)).getItemById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw exception when creating order with non-existent client")
    void testCreateOrder_ClientNotFound() {
        // Given
        Order newOrder = new Order();
        Set<Long> itemIds = new HashSet<>(Arrays.asList(1L));

        when(clientService.getClientById(999L))
                .thenThrow(new ResourceNotFoundException("Client", "id", 999L));

        // When & Then
        assertThatThrownBy(() -> orderService.createOrder(newOrder, 999L, itemIds))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when creating order without items")
    void testCreateOrder_NoItems() {
        // Given
        Order newOrder = new Order();
        newOrder.setPurchaseDate(LocalDate.now());
        Set<Long> emptyItemIds = new HashSet<>();

        when(clientService.getClientById(1L)).thenReturn(testClient);

        // When & Then
        assertThatThrownBy(() -> orderService.createOrder(newOrder, 1L, emptyItemIds))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("at least one item");

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should set purchase date to today if not provided")
    void testCreateOrder_DefaultPurchaseDate() {
        // Given
        Order newOrder = new Order();
        newOrder.setPurchaseDate(null); // No purchase date
        Set<Long> itemIds = new HashSet<>(Arrays.asList(1L));

        when(clientService.getClientById(1L)).thenReturn(testClient);
        when(itemService.getItemById(1L)).thenReturn(testItem);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            assertThat(order.getPurchaseDate()).isEqualTo(LocalDate.now());
            return order;
        });

        // When
        orderService.createOrder(newOrder, 1L, itemIds);

        // Then
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when purchase date is in future")
    void testCreateOrder_FuturePurchaseDate() {
        // Given
        Order newOrder = new Order();
        newOrder.setPurchaseDate(LocalDate.now().plusDays(1)); // Future date
        Set<Long> itemIds = new HashSet<>(Arrays.asList(1L));

        when(clientService.getClientById(1L)).thenReturn(testClient);

        // When & Then
        assertThatThrownBy(() -> orderService.createOrder(newOrder, 1L, itemIds))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("cannot be in the future");

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when delivery date is before purchase date")
    void testCreateOrder_InvalidDeliveryDate() {
        // Given
        Order newOrder = new Order();
        newOrder.setPurchaseDate(LocalDate.now());
        newOrder.setDeliveryDate(LocalDate.now().minusDays(1)); // Before purchase
        Set<Long> itemIds = new HashSet<>(Arrays.asList(1L));

        when(clientService.getClientById(1L)).thenReturn(testClient);

        // When & Then
        assertThatThrownBy(() -> orderService.createOrder(newOrder, 1L, itemIds))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("cannot be before");

        verify(orderRepository, never()).save(any(Order.class));
    }

    // ========================================
    // GET ORDER TESTS
    // ========================================

    @Test
    @DisplayName("Should get order by ID successfully")
    void testGetOrderById_Success() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // When
        Order foundOrder = orderService.getOrderById(1L);

        // Then
        assertThat(foundOrder).isNotNull();
        assertThat(foundOrder.getIdOrder()).isEqualTo(1L);
        assertThat(foundOrder.getClient()).isEqualTo(testClient);

        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when order not found")
    void testGetOrderById_NotFound() {
        // Given
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> orderService.getOrderById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("not found");

        verify(orderRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should get all orders successfully")
    void testGetAllOrders_Success() {
        // Given
        when(orderRepository.findAll()).thenReturn(Arrays.asList(testOrder));

        // When
        List<Order> orders = orderService.getAllOrders();

        // Then
        assertThat(orders).hasSize(1);
        assertThat(orders.get(0)).isEqualTo(testOrder);

        verify(orderRepository, times(1)).findAll();
    }

    // ========================================
    // UPDATE ORDER STATUS TESTS
    // ========================================

    @Test
    @DisplayName("Should update order status from PENDING to PROCESSING")
    void testUpdateOrderStatus_PendingToProcessing() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // When
        Order updatedOrder = orderService.updateOrderStatus(1L, OrderStatus.PROCESSING);

        // Then
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.PROCESSING);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when invalid status transition")
    void testUpdateOrderStatus_InvalidTransition() {
        // Given
        testOrder.setStatus(OrderStatus.PENDING);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // When & Then - Try to go from PENDING to DELIVERED (skip PROCESSING and SHIPPED)
        assertThatThrownBy(() -> orderService.updateOrderStatus(1L, OrderStatus.DELIVERED))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Invalid status transition");

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should not allow status change on completed order")
    void testUpdateOrderStatus_CompletedOrder() {
        // Given
        testOrder.setStatus(OrderStatus.DELIVERED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // When & Then
        assertThatThrownBy(() -> orderService.updateOrderStatus(1L, OrderStatus.PROCESSING))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("completed order");

        verify(orderRepository, never()).save(any(Order.class));
    }

    // ========================================
    // CANCEL ORDER TESTS
    // ========================================

    @Test
    @DisplayName("Should cancel PENDING order successfully")
    void testCancelOrder_Pending() {
        // Given
        testOrder.setStatus(OrderStatus.PENDING);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // When
        Order cancelledOrder = orderService.cancelOrder(1L);

        // Then
        assertThat(cancelledOrder.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Should cancel PROCESSING order successfully")
    void testCancelOrder_Processing() {
        // Given
        testOrder.setStatus(OrderStatus.PROCESSING);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // When
        Order cancelledOrder = orderService.cancelOrder(1L);

        // Then
        assertThat(cancelledOrder.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Should not allow cancelling SHIPPED order")
    void testCancelOrder_Shipped() {
        // Given
        testOrder.setStatus(OrderStatus.SHIPPED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // When & Then
        assertThatThrownBy(() -> orderService.cancelOrder(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Cannot cancel");

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should not allow cancelling DELIVERED order")
    void testCancelOrder_Delivered() {
        // Given
        testOrder.setStatus(OrderStatus.DELIVERED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // When & Then
        assertThatThrownBy(() -> orderService.cancelOrder(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Cannot cancel");

        verify(orderRepository, never()).save(any(Order.class));
    }

    // ========================================
    // ADD/REMOVE ITEMS TESTS
    // ========================================

    @Test
    @DisplayName("Should add items to order successfully")
    void testAddItemsToOrder_Success() {
        // Given
        Item newItem = new Item("New Item", "Description", new BigDecimal("49.99"));
        newItem.setItemId(2L);
        Set<Long> newItemIds = new HashSet<>(Arrays.asList(2L));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(itemService.getItemById(2L)).thenReturn(newItem);
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // When
        Order updatedOrder = orderService.addItemsToOrder(1L, newItemIds);

        // Then
        assertThat(updatedOrder).isNotNull();
        verify(itemService, times(1)).getItemById(2L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Should not allow adding items to completed order")
    void testAddItemsToOrder_CompletedOrder() {
        // Given
        testOrder.setStatus(OrderStatus.DELIVERED);
        Set<Long> newItemIds = new HashSet<>(Arrays.asList(2L));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // When & Then
        assertThatThrownBy(() -> orderService.addItemsToOrder(1L, newItemIds))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("completed order");

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should remove item from order successfully")
    void testRemoveItemFromOrder_Success() {
        // Given
        Item item2 = new Item("Item 2", "Description", new BigDecimal("29.99"));
        item2.setItemId(2L);
        testOrder.addItem(item2); // Order now has 2 items

        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(itemService.getItemById(2L)).thenReturn(item2);
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // When
        Order updatedOrder = orderService.removeItemFromOrder(1L, 2L);

        // Then
        assertThat(updatedOrder).isNotNull();
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Should not allow removing last item from order")
    void testRemoveItemFromOrder_LastItem() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        //when(itemService.getItemById(1L)).thenReturn(testItem); //Line commented because of an error

        // When & Then
        assertThatThrownBy(() -> orderService.removeItemFromOrder(1L, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("at least one item");

        verify(orderRepository, never()).save(any(Order.class));
    }

    // ========================================
    // DELETE ORDER TESTS
    // ========================================

    @Test
    @DisplayName("Should delete PENDING order successfully")
    void testDeleteOrder_Pending() {
        // Given
        testOrder.setStatus(OrderStatus.PENDING);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        doNothing().when(orderRepository).delete(testOrder);

        // When
        orderService.deleteOrder(1L);

        // Then
        verify(orderRepository, times(1)).delete(testOrder);
    }

    @Test
    @DisplayName("Should delete CANCELLED order successfully")
    void testDeleteOrder_Cancelled() {
        // Given
        testOrder.setStatus(OrderStatus.CANCELLED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        doNothing().when(orderRepository).delete(testOrder);

        // When
        orderService.deleteOrder(1L);

        // Then
        verify(orderRepository, times(1)).delete(testOrder);
    }

    @Test
    @DisplayName("Should not delete PROCESSING order")
    void testDeleteOrder_Processing() {
        // Given
        testOrder.setStatus(OrderStatus.PROCESSING);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // When & Then
        assertThatThrownBy(() -> orderService.deleteOrder(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("cancelled or pending");

        verify(orderRepository, never()).delete(any(Order.class));
    }

    // ========================================
    // QUERY TESTS
    // ========================================

    @Test
    @DisplayName("Should get orders by client successfully")
    void testGetOrdersByClient_Success() {
        // Given
        when(clientService.getClientById(1L)).thenReturn(testClient);
        when(orderRepository.findByClientIdClient(1L)).thenReturn(Arrays.asList(testOrder));

        // When
        List<Order> orders = orderService.getOrdersByClient(1L);

        // Then
        assertThat(orders).hasSize(1);
        assertThat(orders.get(0).getClient()).isEqualTo(testClient);

        verify(clientService, times(1)).getClientById(1L);
        verify(orderRepository, times(1)).findByClientIdClient(1L);
    }

    @Test
    @DisplayName("Should get orders by status successfully")
    void testGetOrdersByStatus_Success() {
        // Given
        when(orderRepository.findByStatus(OrderStatus.PENDING))
                .thenReturn(Arrays.asList(testOrder));

        // When
        List<Order> orders = orderService.getOrdersByStatus(OrderStatus.PENDING);

        // Then
        assertThat(orders).hasSize(1);
        assertThat(orders.get(0).getStatus()).isEqualTo(OrderStatus.PENDING);

        verify(orderRepository, times(1)).findByStatus(OrderStatus.PENDING);
    }

    @Test
    @DisplayName("Should get orders in transit successfully")
    void testGetOrdersInTransit_Success() {
        // Given
        testOrder.setStatus(OrderStatus.SHIPPED);
        when(orderRepository.findOrdersInTransit()).thenReturn(Arrays.asList(testOrder));

        // When
        List<Order> orders = orderService.getOrdersInTransit();

        // Then
        assertThat(orders).hasSize(1);
        verify(orderRepository, times(1)).findOrdersInTransit();
    }

    @Test
    @DisplayName("Should count orders by status successfully")
    void testCountOrdersByStatus_Success() {
        // Given
        when(orderRepository.countByStatus(OrderStatus.PENDING)).thenReturn(5L);

        // When
        Long count = orderService.countOrdersByStatus(OrderStatus.PENDING);

        // Then
        assertThat(count).isEqualTo(5L);
        verify(orderRepository, times(1)).countByStatus(OrderStatus.PENDING);
    }
}