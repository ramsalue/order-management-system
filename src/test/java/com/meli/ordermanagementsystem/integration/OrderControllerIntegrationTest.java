package com.meli.ordermanagementsystem.integration;

import com.meli.ordermanagementsystem.dto.OrderDTO;
import com.meli.ordermanagementsystem.dto.OrderResponseDTO;
import com.meli.ordermanagementsystem.model.Client;
import com.meli.ordermanagementsystem.model.Item;
import com.meli.ordermanagementsystem.model.Order;
import com.meli.ordermanagementsystem.model.OrderStatus;
import com.meli.ordermanagementsystem.repository.ClientRepository;
import com.meli.ordermanagementsystem.repository.ItemRepository;
import com.meli.ordermanagementsystem.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Order API endpoints
 * Tests complete order management workflow
 */
public class OrderControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ItemRepository itemRepository;

    private Client testClient;
    private Item testItem1;
    private Item testItem2;

    @BeforeEach
    public void setUp() {
        // Clean up
        orderRepository.deleteAll();
        itemRepository.deleteAll();
        clientRepository.deleteAll();

        // Create test data
        testClient = clientRepository.save(
                new Client("Test Client", "Test Address", 30)
        );

        testItem1 = itemRepository.save(
                new Item("Test Item 1", "Description 1", new BigDecimal("100.00"))
        );

        testItem2 = itemRepository.save(
                new Item("Test Item 2", "Description 2", new BigDecimal("200.00"))
        );
    }

    /**
     * Test creating an order
     */
    @Test
    public void testCreateOrder_Success() throws Exception {
        // Given
        OrderDTO orderDTO = new OrderDTO(
                null,
                testClient.getIdClient(),
                LocalDate.now(),
                LocalDate.now().plusDays(7),
                null,
                Set.of(testItem1.getItemId(), testItem2.getItemId())
        );

        // When & Then
        MvcResult result = mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(orderDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idOrder").exists())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.client").exists())
                .andExpect(jsonPath("$.client.name").value("Test Client"))
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andReturn();

        // Verify in database
        String responseJson = result.getResponse().getContentAsString();
        OrderResponseDTO createdOrder = fromJsonString(responseJson, OrderResponseDTO.class);

        Order orderInDb = orderRepository.findById(createdOrder.getIdOrder()).orElse(null);
        assertThat(orderInDb).isNotNull();
        assertThat(orderInDb.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(orderInDb.getItems()).hasSize(2);
    }

    /**
     * Test creating order without items (should fail)
     */
    @Test
    public void testCreateOrder_NoItems_Failure() throws Exception {
        // Given
        OrderDTO orderDTO = new OrderDTO(
                null,
                testClient.getIdClient(),
                LocalDate.now(),
                LocalDate.now().plusDays(7),
                null,
                Set.of() // Empty items
        );

        // When & Then
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(orderDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.itemIds").exists());
    }

    /**
     * Test creating order with non-existent client
     */
    @Test
    public void testCreateOrder_NonExistentClient() throws Exception {
        // Given
        OrderDTO orderDTO = new OrderDTO(
                null,
                99999L, // Non-existent client
                LocalDate.now(),
                LocalDate.now().plusDays(7),
                null,
                Set.of(testItem1.getItemId())
        );

        // When & Then
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(orderDTO)))
                .andExpect(status().isNotFound());
    }

    /**
     * Test getting order by ID with full details
     */
    @Test
    public void testGetOrderById_WithFullDetails() throws Exception {
        // Given
        Order order = new Order(LocalDate.now(), testClient);
        order.addItem(testItem1);
        order.addItem(testItem2);
        order.setDeliveryDate(LocalDate.now().plusDays(5));
        Order savedOrder = orderRepository.save(order);

        // When & Then
        mockMvc.perform(get("/api/orders/" + savedOrder.getIdOrder()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idOrder").value(savedOrder.getIdOrder()))
                .andExpect(jsonPath("$.client").exists())
                .andExpect(jsonPath("$.client.name").value("Test Client"))
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    /**
     * Test getting all orders
     */
    @Test
    public void testGetAllOrders_Success() throws Exception {
        // Given
        Order order1 = new Order(LocalDate.now(), testClient);
        order1.addItem(testItem1);
        orderRepository.save(order1);

        Order order2 = new Order(LocalDate.now(), testClient);
        order2.addItem(testItem2);
        orderRepository.save(order2);

        // When & Then
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].client").exists())
                .andExpect(jsonPath("$[0].items").exists())
                .andExpect(jsonPath("$[1].client").exists())
                .andExpect(jsonPath("$[1].items").exists());
    }

    /**
     * Test updating order status
     */
    @Test
    public void testUpdateOrderStatus_Success() throws Exception {
        // Given
        Order order = new Order(LocalDate.now(), testClient);
        order.addItem(testItem1);
        Order savedOrder = orderRepository.save(order);

        // When & Then - Update to PROCESSING
        mockMvc.perform(patch("/api/orders/" + savedOrder.getIdOrder() + "/status")
                        .param("status", "PROCESSING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PROCESSING"));

        // Verify in database
        Order updatedOrder = orderRepository.findById(savedOrder.getIdOrder()).orElse(null);
        assertThat(updatedOrder).isNotNull();
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.PROCESSING);
    }

    /**
     * Test invalid status transition
     */
    @Test
    public void testUpdateOrderStatus_InvalidTransition() throws Exception {
        // Given - order in PENDING status
        Order order = new Order(LocalDate.now(), testClient);
        order.addItem(testItem1);
        Order savedOrder = orderRepository.save(order);

        // When & Then - Try to update directly to DELIVERED (invalid)
        mockMvc.perform(patch("/api/orders/" + savedOrder.getIdOrder() + "/status")
                        .param("status", "DELIVERED"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Invalid status transition")));
    }

    /**
     * Test cancelling order
     */
    @Test
    public void testCancelOrder_Success() throws Exception {
        // Given
        Order order = new Order(LocalDate.now(), testClient);
        order.addItem(testItem1);
        Order savedOrder = orderRepository.save(order);

        // When & Then
        mockMvc.perform(post("/api/orders/" + savedOrder.getIdOrder() + "/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        // Verify in database
        Order cancelledOrder = orderRepository.findById(savedOrder.getIdOrder()).orElse(null);
        assertThat(cancelledOrder).isNotNull();
        assertThat(cancelledOrder.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    /**
     * Test cannot cancel already shipped order
     */
    @Test
    public void testCancelOrder_AlreadyShipped() throws Exception {
        // Given - order in SHIPPED status
        Order order = new Order(LocalDate.now(), testClient);
        order.addItem(testItem1);
        order.setStatus(OrderStatus.SHIPPED);
        Order savedOrder = orderRepository.save(order);

        // When & Then
        mockMvc.perform(post("/api/orders/" + savedOrder.getIdOrder() + "/cancel"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Cannot cancel")));
    }

    /**
     * Test adding items to order
     */
    @Test
    public void testAddItemsToOrder_Success() throws Exception {
        // Given - order with one item
        Order order = new Order(LocalDate.now(), testClient);
        order.addItem(testItem1);
        Order savedOrder = orderRepository.save(order);

        // Create additional item
        Item newItem = itemRepository.save(
                new Item("New Item", "New Description", new BigDecimal("150.00"))
        );

        // When & Then - Add new item
        mockMvc.perform(post("/api/orders/" + savedOrder.getIdOrder() + "/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(Set.of(newItem.getItemId()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(2)));

        // Verify in database
        Order updatedOrder = orderRepository.findById(savedOrder.getIdOrder()).orElse(null);
        assertThat(updatedOrder).isNotNull();
        assertThat(updatedOrder.getItems()).hasSize(2);
    }

    /**
     * Test removing item from order
     */
    @Test
    public void testRemoveItemFromOrder_Success() throws Exception {
        // Given - order with two items
        Order order = new Order(LocalDate.now(), testClient);
        order.addItem(testItem1);
        order.addItem(testItem2);
        Order savedOrder = orderRepository.save(order);

        // When & Then - Remove one item
        mockMvc.perform(delete("/api/orders/" + savedOrder.getIdOrder() + "/items/" + testItem2.getItemId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(1)));

        // Verify in database
        Order updatedOrder = orderRepository.findById(savedOrder.getIdOrder()).orElse(null);
        assertThat(updatedOrder).isNotNull();
        assertThat(updatedOrder.getItems()).hasSize(1);
    }

    /**
     * Test cannot remove last item from order
     */
    @Test
    public void testRemoveItemFromOrder_LastItem_Failure() throws Exception {
        // Given - order with only one item
        Order order = new Order(LocalDate.now(), testClient);
        order.addItem(testItem1);
        Order savedOrder = orderRepository.save(order);

        // When & Then - Try to remove the only item
        mockMvc.perform(delete("/api/orders/" + savedOrder.getIdOrder() + "/items/" + testItem1.getItemId()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("at least one item")));
    }

    /**
     * Test getting orders by client
     */
    @Test
    public void testGetOrdersByClient_Success() throws Exception {
        // Given
        Order order1 = new Order(LocalDate.now(), testClient);
        order1.addItem(testItem1);
        orderRepository.save(order1);

        Order order2 = new Order(LocalDate.now(), testClient);
        order2.addItem(testItem2);
        orderRepository.save(order2);

        // When & Then
        mockMvc.perform(get("/api/orders/client/" + testClient.getIdClient()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    /**
     * Test getting orders by status
     */
    @Test
    public void testGetOrdersByStatus_Success() throws Exception {
        // Given
        Order pendingOrder = new Order(LocalDate.now(), testClient);
        pendingOrder.addItem(testItem1);
        orderRepository.save(pendingOrder);

        Order processingOrder = new Order(LocalDate.now(), testClient);
        processingOrder.addItem(testItem2);
        processingOrder.setStatus(OrderStatus.PROCESSING);
        orderRepository.save(processingOrder);

        // When & Then - Get PENDING orders
        mockMvc.perform(get("/api/orders/status/PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status").value("PENDING"));

        // Get PROCESSING orders
        mockMvc.perform(get("/api/orders/status/PROCESSING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status").value("PROCESSING"));
    }

    /**
     * Test deleting order
     */
    @Test
    public void testDeleteOrder_Success() throws Exception {
        // Given - PENDING order (can be deleted)
        Order order = new Order(LocalDate.now(), testClient);
        order.addItem(testItem1);
        Order savedOrder = orderRepository.save(order);

        // When & Then
        mockMvc.perform(delete("/api/orders/" + savedOrder.getIdOrder()))
                .andExpect(status().isNoContent());

        // Verify deleted
        assertThat(orderRepository.findById(savedOrder.getIdOrder())).isEmpty();
    }

    /**
     * Test cannot delete shipped order
     */
    @Test
    public void testDeleteOrder_Shipped_Failure() throws Exception {
        // Given - SHIPPED order
        Order order = new Order(LocalDate.now(), testClient);
        order.addItem(testItem1);
        order.setStatus(OrderStatus.SHIPPED);
        Order savedOrder = orderRepository.save(order);

        // When & Then
        mockMvc.perform(delete("/api/orders/" + savedOrder.getIdOrder()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Can only delete")));
    }

    /**
     * Test complete order workflow
     */
    @Test
    public void testCompleteOrderWorkflow() throws Exception {
        // 1. Create order
        OrderDTO createDTO = new OrderDTO(
                null,
                testClient.getIdClient(),
                LocalDate.now(),
                LocalDate.now().plusDays(7),
                null,
                Set.of(testItem1.getItemId())
        );

        MvcResult createResult = mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(createDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        OrderResponseDTO createdOrder = fromJsonString(
                createResult.getResponse().getContentAsString(),
                OrderResponseDTO.class
        );
        Long orderId = createdOrder.getIdOrder();

        // 2. Update to PROCESSING
        mockMvc.perform(patch("/api/orders/" + orderId + "/status")
                        .param("status", "PROCESSING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PROCESSING"));

        // 3. Add another item
        mockMvc.perform(post("/api/orders/" + orderId + "/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(Set.of(testItem2.getItemId()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(2)));

        // 4. Update to SHIPPED
        mockMvc.perform(patch("/api/orders/" + orderId + "/status")
                        .param("status", "SHIPPED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SHIPPED"));

        // 5. Update to DELIVERED
        mockMvc.perform(patch("/api/orders/" + orderId + "/status")
                        .param("status", "DELIVERED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DELIVERED"));

        // 6. Verify final state
        mockMvc.perform(get("/api/orders/" + orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DELIVERED"))
                .andExpect(jsonPath("$.items", hasSize(2)));
    }
}