package dev.barahow.core.exceptions;

public class OrderHistoryNotFound extends RuntimeException {
    public OrderHistoryNotFound(String message) {
        super(message);
    }
}
