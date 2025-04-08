package dev.barahow.core.dto;

import java.math.BigDecimal;
import java.util.UUID;


public class OrderItem {
    private UUID productId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
public OrderItem(){}
    public OrderItem(UUID productId, String productName, Integer quantity, BigDecimal price) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }

    public UUID getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }
}
