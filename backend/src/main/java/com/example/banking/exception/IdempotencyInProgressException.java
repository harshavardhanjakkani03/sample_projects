package com.example.banking.exception;

public class IdempotencyInProgressException extends RuntimeException {
    public IdempotencyInProgressException(String message) {
        super(message);
    }
}
