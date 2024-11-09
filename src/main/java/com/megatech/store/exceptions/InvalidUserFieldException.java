package com.megatech.store.exceptions;

public class InvalidUserFieldException extends RuntimeException {
    public InvalidUserFieldException(String message) {
        super(message);
    }
}
