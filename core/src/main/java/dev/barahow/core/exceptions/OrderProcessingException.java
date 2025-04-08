package dev.barahow.core.exceptions;

public class OrderProcessingException extends RuntimeException {
    public OrderProcessingException(String message) {
        super(message);
    }

    public OrderProcessingException(String message,Exception ex) {
        super(message,ex);
    }
}
