package dev.barahow.order_microservice.service;

import dev.barahow.core.dto.Order;
import dev.barahow.order_microservice.dao.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

public interface OrderService  {
   Order getOrderById(UUID orderId);
    Order placeOrder(Order order);

    void approveOrder(UUID orderId);

    void rejectOrder(UUID orderId);
    void cancelOrder(UUID orderId);
    void orderShipped(UUID orderId);
    void orderDelivered(UUID orderId);
    void orderReturned(UUID orderId);
    void orderFailed(UUID orderId);

    List<Order> findOrderByCustomer(UUID customerId);
}
