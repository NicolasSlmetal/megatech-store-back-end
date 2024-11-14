package com.megatech.store.exceptions;

public class InvalidPurchaseFieldException extends RuntimeException {
    public InvalidPurchaseFieldException(String message) {
        super(message);
    }
}
