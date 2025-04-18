package dev.barahow.order_microservice.mapper;

import dev.barahow.core.dto.OrderItem;
import dev.barahow.order_microservice.dao.OrderItemEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Component
public class OrderItemsMapper {
//    private UUID productId;
//    private String productName;
//    private Integer quantity;
//    private BigDecimal price;
   OrderItem toDTO(UUID productId, String productName, Integer quantity, BigDecimal price){
        return new OrderItem(productId,productName,quantity,price);
    }

    OrderItemEntity toEntity(UUID productId, String productName,Integer quantity,BigDecimal price) {
        return new OrderItemEntity(productId,productName,quantity,price);
    }

}
