package dev.barahow.core.exceptions;

public class InvalidOrderHistoryException extends RuntimeException {
    public InvalidOrderHistoryException(String message) {
        super(message);
    }
}
