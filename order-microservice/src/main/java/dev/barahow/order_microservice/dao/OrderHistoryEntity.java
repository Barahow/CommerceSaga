package dev.barahow.order_microservice.dao;

import dev.barahow.core.types.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.UUID;


@Entity
@Table(name = "orders_history")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class OrderHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "order_id")
    private UUID orderId;
    @Column(name = "status")
    private OrderStatus status;
    @Column(name = "created_at")
    private Timestamp createdAt;
}
