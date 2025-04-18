package dev.barahow.order_microservice.mapper;


import dev.barahow.core.dto.Order;
import dev.barahow.core.dto.OrderItem;
import dev.barahow.core.dto.UserDTO;
import dev.barahow.order_microservice.dao.OrderEntity;
import dev.barahow.order_microservice.dao.OrderItemEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapperImp implements OrderMapper {

    private final OrderItemsMapper orderItemsMapper;

    public OrderMapperImp(OrderItemsMapper orderItemsMapper) {
        this.orderItemsMapper = orderItemsMapper;
    }

    @Override
    public Order toDTO(OrderEntity orderEntity) {
        if (orderEntity == null) {
            return null;
        }
        List<OrderItem> orderItem = orderEntity.getItems().stream()
                .map(item -> orderItemsMapper.toDTO(
                        item.getProductId(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getPrice()
                ))
                .collect(Collectors.toList());

        return Order.builder().
                orderId(orderEntity.getId())
                .customerId(orderEntity.getCustomerId())
                .status(orderEntity.getStatus())
                .createdAt(orderEntity.getCreatedAt())
                .items(orderItem)
                .paymentId(orderEntity.getPaymentId())
                .shipmentId(orderEntity.getShipmentId())

                .build();
    }

    @Override
    public OrderEntity toEntity(Order orderDTO) {
        if (orderDTO == null) {
            return null;
        }
        // Correctly map List<OrderItemEntity> to List<OrderItemDTO>
        List<OrderItemEntity> orderItemEntity = orderDTO.getItems().stream()
                .map(item -> orderItemsMapper.toEntity(
                        item.getProductId(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getPrice()
                ))
                .collect(Collectors.toList());


        return OrderEntity.builder()
                .id(orderDTO.getOrderId())
                .customerId(orderDTO.getCustomerId())
                .paymentId(orderDTO.getPaymentId())
                .status(orderDTO.getStatus())
                .shipmentId(orderDTO.getShipmentId())
                .updatedAt(orderDTO.getUpdatedAt())
                .items(orderItemEntity)
                .build();
    }

}

