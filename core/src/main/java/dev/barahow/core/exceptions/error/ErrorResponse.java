package dev.barahow.core.exceptions.error;

import java.time.LocalDateTime;

public class ErrorResponse {
    private LocalDateTime timeStamp;
    private String message;
    private String details;
    private String errorCode;

    public ErrorResponse(LocalDateTime timeStamp, String message, String details, String errorCode) {
        this.timeStamp = timeStamp;
        this.message = message;
        this.details = details;
        this.errorCode = errorCode;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
