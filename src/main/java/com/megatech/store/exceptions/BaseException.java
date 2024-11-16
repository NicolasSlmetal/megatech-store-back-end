package com.megatech.store.exceptions;

import org.springframework.http.HttpStatus;

public class BaseException extends RuntimeException {

    private final ErrorType errorType;
    private HttpStatus httpStatus;
    public BaseException(String message, ErrorType errorType, HttpStatus httpStatus) {
        super(message);
        this.errorType = errorType;
        this.httpStatus = httpStatus;
    }

    public ErrorType getErrorType() {
        return errorType;
    }
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
