package dev.barahow.order_microservice.dto;

import dev.barahow.core.types.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateOrderResponse {
    private UUID orderId;
    private UUID customerId;
    private UUID productId;
    private Integer productQuantity;
    private OrderStatus orderStatus;

}
