package dev.barahow.order_microservice.repository;

import dev.barahow.order_microservice.dao.OrderEntity;
import dev.barahow.order_microservice.dao.OrderHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {

    List<OrderEntity> findByCustomerIdOrderByCreatedAtDesc(UUID orderId);

}
