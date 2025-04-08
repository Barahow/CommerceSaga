package dev.barahow.core.dto;

import dev.barahow.core.types.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Order {
    private UUID orderId;
    private UUID customerId;

    private OrderStatus status;
    private List<OrderItem> items;


    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private UUID paymentId;         // reference to payment transaction
    private UUID shipmentId;

    public UUID getOrderId() {
        return orderId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public UUID getPaymentId() {
        return paymentId;
    }

    public UUID getShipmentId() {
        return shipmentId;
    }

    public Order() {
    }


    // Private constructor used by the builder
    private Order(Builder builder) {
        this.orderId = builder.orderId;
        this.customerId = builder.customerId;
        this.status = builder.status;
        this.items = builder.items;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
        this.paymentId = builder.paymentId;
        this.shipmentId = builder.shipmentId;
    }

    // Static inner Builder class
    public static class Builder {
        private UUID orderId;
        private UUID customerId;
        private OrderStatus status;
        private List<OrderItem> items;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private UUID paymentId;
        private UUID shipmentId;

        public Builder orderId(UUID orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder customerId(UUID customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder status(OrderStatus status) {
            this.status = status;
            return this;
        }

        public Builder items(List<OrderItem> items) {
            this.items = items;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder paymentId(UUID paymentId) {
            this.paymentId = paymentId;
            return this;
        }

        public Builder shipmentId(UUID shipmentId) {
            this.shipmentId = shipmentId;
            return this;
        }

        public Order build() {
            return new Order(this);
        }
    }

    // Optional: static method for starting the builder
    public static Builder builder() {
        return new Builder();
    }
}
