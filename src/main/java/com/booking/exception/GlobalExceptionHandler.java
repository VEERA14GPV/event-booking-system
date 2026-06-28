package com.booking.exception;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;

import org.springframework.validation.FieldError;

import org.springframework.web.bind.MethodArgumentNotValidException;

import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.booking.dto.response.ErrorResponse;

import java.time.LocalDateTime;

import java.util.HashMap;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /*
     * Handle resource not found exceptions.
     */
    @ExceptionHandler(
            ResourceNotFoundException.class
    )
    public ResponseEntity<ErrorResponse>
    handleResourceNotFound(
            ResourceNotFoundException ex) {

        ErrorResponse error =
                new ErrorResponse(

                        ex.getMessage(),

                        HttpStatus.NOT_FOUND.value(),

                        LocalDateTime.now()
                );

        return new ResponseEntity<>(

                error,

                HttpStatus.NOT_FOUND
        );
    }

    /*
     * Handle idempotency exceptions.
     */
    @ExceptionHandler(
            IdempotencyException.class
    )
    public ResponseEntity<ErrorResponse>
    handleIdempotencyException(
            IdempotencyException ex) {

        ErrorResponse error =
                new ErrorResponse(

                        ex.getMessage(),

                        HttpStatus.CONFLICT.value(),

                        LocalDateTime.now()
                );

        return new ResponseEntity<>(

                error,

                HttpStatus.CONFLICT
        );
    }

    /*
     * Handle validation exceptions.
     */
    @ExceptionHandler(
            MethodArgumentNotValidException.class
    )
    public ResponseEntity<Map<String, String>>
    handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors =
                new HashMap<>();

        ex.getBindingResult()

                .getAllErrors()

                .forEach(error -> {

                    String fieldName =

                            ((FieldError) error)
                                    .getField();

                    String errorMessage =
                            error.getDefaultMessage();

                    errors.put(
                            fieldName,
                            errorMessage
                    );
                });

        return new ResponseEntity<>(

                errors,

                HttpStatus.BAD_REQUEST
        );
    }

    /*
     * Handle illegal arguments.
     */
    @ExceptionHandler(
            IllegalArgumentException.class
    )
    public ResponseEntity<ErrorResponse>
    handleIllegalArgument(
            IllegalArgumentException ex) {

        ErrorResponse error =
                new ErrorResponse(

                        ex.getMessage(),

                        HttpStatus.BAD_REQUEST.value(),

                        LocalDateTime.now()
                );

        return new ResponseEntity<>(

                error,

                HttpStatus.BAD_REQUEST
        );
    }

    /*
     * Handle generic exceptions.
     */
    @ExceptionHandler(
            Exception.class
    )
    public ResponseEntity<ErrorResponse>
    handleGeneric(
            Exception ex) {

        ErrorResponse error =
                new ErrorResponse(

                        ex.getMessage(),

                        HttpStatus.INTERNAL_SERVER_ERROR.value(),

                        LocalDateTime.now()
                );

        return new ResponseEntity<>(

                error,

                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
    
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex) {
        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.CONFLICT.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }
    
}
