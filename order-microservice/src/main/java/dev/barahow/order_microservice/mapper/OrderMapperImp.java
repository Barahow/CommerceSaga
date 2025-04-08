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
    @Override
    public Order toDTO(OrderEntity orderEntity) {
        if (orderEntity == null) {
            return null;
        }
        // Correctly map List<OrderItemEntity> to List<OrderItemDTO>
        List<OrderItem> orderItemDTOs = orderEntity.getItems().stream()
                .map(orderItemEntity -> toDTO(orderItemEntity))  // Explicitly calling toDTO
                .collect(Collectors.toList());



        return Order.builder().
                orderId(orderEntity.getId())
                .customerId(orderEntity.getCustomerId())
                .status(orderEntity.getStatus())
                .createdAt(orderEntity.getCreatedAt())
                .items(orderItemDTOs)
                .paymentId(orderEntity.getPaymentId())
                .shipmentId(orderEntity.getShipmentId())

                .build();
    }

    @Override
    public OrderEntity toEntity(Order orderDTO) {
        if(orderDTO==null){
            return null;
        }
        // Correctly map List<OrderItemEntity> to List<OrderItemDTO>
        List<OrderItemEntity> orderItemDTOs = orderDTO.getItems().stream()
                .map(orderItemEntity -> toEntity(orderItemEntity))  // Explicitly calling toDTO
                .collect(Collectors.toList());


        return OrderEntity.builder()
                .id(orderDTO.getOrderId())
                .customerId(orderDTO.getCustomerId())
                .paymentId(orderDTO.getPaymentId())
                .status(orderDTO.getStatus())
                .shipmentId(orderDTO.getShipmentId())
                .updatedAt(orderDTO.getUpdatedAt())
                .items(orderItemDTOs)
                .build();
    }

    //method to map OrderItemDTO to orderItemEntity
    private OrderItemEntity toEntity(OrderItem orderItem){
        if (orderItem==null){
            return null;
        }

        return new OrderItemEntity(orderItem.getProductId(),orderItem.getProductName(),orderItem.getQuantity(),orderItem.getPrice());
    }
    // Method to map OrderItemEntity to OrderItemDTO
    private OrderItem toDTO(OrderItemEntity orderItemEntity) {
        if (orderItemEntity == null) {
            return null;
        }

        return new OrderItem(
                orderItemEntity.getProductId(),
                orderItemEntity.getProductName(),
                orderItemEntity.getQuantity(),
                orderItemEntity.getPrice()
        );
    }

    }


