package com.example.banking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(IdempotencyException.class)
    public ResponseEntity<String> handleIdempotency(IdempotencyException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(com.example.banking.exception.IdempotencyInProgressException.class)
    public ResponseEntity<String> handleIdempotencyInProgress(com.example.banking.exception.IdempotencyInProgressException ex) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntime(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
