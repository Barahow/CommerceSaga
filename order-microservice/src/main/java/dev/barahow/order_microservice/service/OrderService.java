package dev.barahow.order_microservice.service;

import dev.barahow.core.dto.Order;
import dev.barahow.order_microservice.dao.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

public interface OrderService  {

    Order placeOrder(Order order);

    void approveOrder(UUID orderId);

    void rejectOrder(UUID orderId);
}
