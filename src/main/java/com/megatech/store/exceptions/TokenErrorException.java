package com.megatech.store.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class TokenErrorException extends BaseException {
    public TokenErrorException(String message) {
        super(message, ErrorType.ACCESS_TOKEN_EXPIRED_OR_DENIED, HttpStatus.UNAUTHORIZED);
    }
}
