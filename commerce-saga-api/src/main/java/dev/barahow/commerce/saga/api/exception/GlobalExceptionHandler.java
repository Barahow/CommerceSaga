package dev.barahow.commerce.saga.api.exception;

import dev.barahow.core.exceptions.*;
import dev.barahow.core.exceptions.error.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;

import java.nio.file.AccessDeniedException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler({
            AccessDeniedException.class,
            MethodArgumentNotValidException.class,
            ConstraintViolationException.class,
            AuthenticationException.class,
            OrderNotFoundException.class,
            UserNotFoundException.class,
            UserAlreadyExistsException.class,
            HttpRequestMethodNotSupportedException.class,
            HttpMediaTypeNotSupportedException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<ErrorResponse> handleCommonExceptions(Exception ex, WebRequest request){


        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                determineErrorCode(ex),
                ex.getMessage(),
                determineHTTPStatusCode(ex).value()



        );

        return new ResponseEntity<>(error,determineHTTPStatusCode(ex));
    }

    private HttpStatus determineHTTPStatusCode(Exception ex) {
        if (ex instanceof AccessDeniedException){
            return HttpStatus.FORBIDDEN; //403
        }
        if (ex instanceof MethodArgumentNotValidException|| ex instanceof ConstraintViolationException || ex instanceof IllegalArgumentException) {
            return HttpStatus.BAD_REQUEST; //400
        }
        if (ex instanceof HttpClientErrorException.Unauthorized || ex instanceof AuthenticationException){
            return HttpStatus.UNAUTHORIZED; //401
        }

        if(ex instanceof  ProductNotFoundException||ex instanceof OrderNotFoundException
                || ex instanceof UserNotFoundException ||
                ex instanceof OrderHistoryNotFound ||
                ex instanceof RoleNotFoundException) {
            return HttpStatus.NOT_FOUND;
        }
        if (ex instanceof HttpRequestMethodNotSupportedException) {
            return HttpStatus.METHOD_NOT_ALLOWED;//405
        }
        if (ex instanceof HttpMediaTypeNotSupportedException){
            return HttpStatus.UNSUPPORTED_MEDIA_TYPE;//415
        }

        //This exception occurs when malformed JSON is sent to the server.
        if (ex instanceof HttpMessageNotReadableException || ex instanceof InvalidOrderException || ex instanceof InvalidOrderHistoryException) {
            return HttpStatus.BAD_REQUEST;
        }
        if(ex instanceof UserAlreadyExistsException || ex instanceof InsufficientProductQuantityException){
            return HttpStatus.CONFLICT;//409
        }



        //default to internal Server error for unhandled exceptions
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private String determineErrorCode(Exception ex) {

        return ex.getClass().getSimpleName();
    }


}
