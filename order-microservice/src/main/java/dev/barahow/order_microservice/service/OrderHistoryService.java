package dev.barahow.order_microservice.service;

import dev.barahow.core.types.OrderStatus;
import dev.barahow.order_microservice.dto.OrderHistory;

import java.util.List;
import java.util.UUID;

public interface OrderHistoryService {
    void add(UUID orderId, OrderStatus orderStatus);

    List<OrderHistory> findByOrderId(UUID orderId);
}
