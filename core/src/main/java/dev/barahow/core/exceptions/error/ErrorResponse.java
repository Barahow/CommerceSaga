package dev.barahow.core.exceptions.error;

import java.time.LocalDateTime;

public class ErrorResponse {
    private LocalDateTime timeStamp;
    private String errorCode;
    private String message;
    private int status;

    public ErrorResponse(LocalDateTime timeStamp, String errorCode, String message, int status) {
        this.timeStamp = timeStamp;
        this.errorCode = errorCode;
        this.message = message;
        this.status = status;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }
}