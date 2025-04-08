package dev.barahow.order_microservice.dao;

import dev.barahow.core.dto.OrderItem;
import dev.barahow.core.types.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Table(name = "orders")
@Entity

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_id")
    private UUID id;

    @Column(name = "customer_id")
    private UUID customerId;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
     private OrderStatus status;


    // OrderItemENtity doesnt have an id on its on
    // Doesnt need its own repository or service
    // its a list so we cant use @Embedded
    @ElementCollection
    @CollectionTable(name = "order_items",joinColumns = @JoinColumn(name = "order_id"))
    private List<OrderItemEntity> items;
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private UUID paymentId;// reference to payment transaction

    private UUID shipmentId;


}
