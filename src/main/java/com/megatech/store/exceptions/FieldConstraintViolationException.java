package com.megatech.store.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FieldConstraintViolationException extends BaseException {
    public FieldConstraintViolationException(String message, ErrorType errorType) {
        super(message, errorType, HttpStatus.BAD_REQUEST);
    }
}
