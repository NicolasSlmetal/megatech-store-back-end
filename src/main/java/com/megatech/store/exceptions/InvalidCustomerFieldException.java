package com.megatech.store.exceptions;

public class InvalidCustomerFieldException extends RuntimeException {
    public InvalidCustomerFieldException(String message) {
        super(message);
    }
}
