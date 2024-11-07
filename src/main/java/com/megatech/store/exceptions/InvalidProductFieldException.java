package com.megatech.store.exceptions;

public class InvalidProductFieldException extends RuntimeException {
    public InvalidProductFieldException(String message) {
        super(message);
    }
}
