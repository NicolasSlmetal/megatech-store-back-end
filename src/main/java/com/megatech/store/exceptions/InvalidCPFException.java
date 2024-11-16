package com.megatech.store.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidCPFException extends InvalidCustomerFieldException {
    public InvalidCPFException(String message) {
        super(message, ErrorType.INVALID_CUSTOMER_CPF);
    }
}
