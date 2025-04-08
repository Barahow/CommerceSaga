package dev.barahow.order_microservice.dao;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class OrderItemEntity {
        private UUID productId;
        private String productName;
        private int quantity;
        private BigDecimal price;


}


