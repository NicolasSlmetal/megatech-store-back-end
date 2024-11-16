package com.megatech.store.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EntityNotFoundException extends BaseException {
    public EntityNotFoundException(String message, ErrorType errorType) {
        super(message, errorType, HttpStatus.NOT_FOUND);
    }
}
