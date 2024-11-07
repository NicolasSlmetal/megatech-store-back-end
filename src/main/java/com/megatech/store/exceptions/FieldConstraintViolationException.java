package com.megatech.store.exceptions;

public class FieldConstraintViolationException extends RuntimeException {
    public FieldConstraintViolationException(String message) {
        super(message);
    }
}
