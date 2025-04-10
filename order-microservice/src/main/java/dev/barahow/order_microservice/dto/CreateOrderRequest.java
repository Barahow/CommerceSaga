package dev.barahow.order_microservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CreateOrderRequest {
    @NotNull
    private UUID customerId;
    @NotNull
    private UUID productId;
    @NotNull
    private Integer productQuantity;
}
