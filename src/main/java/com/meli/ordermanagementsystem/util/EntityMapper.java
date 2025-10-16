package com.meli.ordermanagementsystem.util;

import com.meli.ordermanagementsystem.dto.*;
import com.meli.ordermanagementsystem.model.Client;
import com.meli.ordermanagementsystem.model.Item;
import com.meli.ordermanagementsystem.model.Order;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for mapping between entities and DTOs
 * Provides conversion methods to separate API layer from domain layer
 */
public class EntityMapper {

    /**
     * Private constructor to prevent instantiation
     */
    private EntityMapper() {
    }

    /**
     * Converts Client entity to ClientDTO
     * @param client the client entity
     * @return ClientDTO
     */
    public static ClientDTO toClientDTO(Client client) {
        if (client == null) {
            return null;
        }
        return new ClientDTO(
                client.getIdClient(),
                client.getName(),
                client.getAddress(),
                client.getAge()
        );
    }

    /**
     * Converts ClientDTO to Client entity
     * @param clientDTO the client DTO
     * @return Client entity
     */
    public static Client toClientEntity(ClientDTO clientDTO) {
        if (clientDTO == null) {
            return null;
        }
        Client client = new Client(
                clientDTO.getName(),
                clientDTO.getAddress(),
                clientDTO.getAge()
        );
        client.setIdClient(clientDTO.getIdClient());
        return client;
    }

    /**
     * Converts Item entity to ItemDTO
     * @param item the item entity
     * @return ItemDTO
     */
    public static ItemDTO toItemDTO(Item item) {
        if (item == null) {
            return null;
        }
        return new ItemDTO(
                item.getItemId(),
                item.getName(),
                item.getDescription(),
                item.getPrice()
        );
    }

    /**
     * Converts ItemDTO to Item entity
     * @param itemDTO the item DTO
     * @return Item entity
     */
    public static Item toItemEntity(ItemDTO itemDTO) {
        if (itemDTO == null) {
            return null;
        }
        Item item = new Item(
                itemDTO.getName(),
                itemDTO.getDescription(),
                itemDTO.getPrice()
        );
        item.setItemId(itemDTO.getItemId());
        return item;
    }

    /**
     * Converts Order entity to OrderResponseDTO with full details
     * @param order the order entity
     * @return OrderResponseDTO with client and items
     */
    public static OrderResponseDTO toOrderResponseDTO(Order order) {
        if (order == null) {
            return null;
        }

        ClientDTO clientDTO = toClientDTO(order.getClient());

        List<ItemDTO> itemDTOs = order.getItems().stream()
                .map(EntityMapper::toItemDTO)
                .collect(Collectors.toList());

        return new OrderResponseDTO(
                order.getIdOrder(),
                clientDTO,
                order.getPurchaseDate(),
                order.getDeliveryDate(),
                order.getStatus(),
                itemDTOs
        );
    }

    /**
     * Converts Order entity to simple OrderDTO
     * @param order the order entity
     * @return OrderDTO
     */
    public static OrderDTO toOrderDTO(Order order) {
        if (order == null) {
            return null;
        }

        return new OrderDTO(
                order.getIdOrder(),
                order.getClient().getIdClient(),
                order.getPurchaseDate(),
                order.getDeliveryDate(),
                order.getStatus(),
                order.getItems().stream()
                        .map(Item::getItemId)
                        .collect(Collectors.toSet())
        );
    }

    /**
     * Converts OrderDTO to Order entity (without relationships)
     * Relationships must be set separately
     * @param orderDTO the order DTO
     * @return Order entity
     */
    public static Order toOrderEntity(OrderDTO orderDTO) {
        if (orderDTO == null) {
            return null;
        }

        Order order = new Order();
        order.setIdOrder(orderDTO.getIdOrder());
        order.setPurchaseDate(orderDTO.getPurchaseDate());
        order.setDeliveryDate(orderDTO.getDeliveryDate());
        order.setStatus(orderDTO.getStatus());

        return order;
    }
}