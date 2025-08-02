package com.megatech.store.configuration;

import com.megatech.store.exceptions.*;
import jakarta.persistence.ElementCollection;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class ExceptionDispatcher {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<SingleErrorResponse> handleBaseException(BaseException e) {
        return new ResponseEntity<>(new SingleErrorResponse(e.getMessage(), e.getErrorType()), e.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationError(MethodArgumentNotValidException e) {
        String[] errors = e.getFieldErrors().stream().map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("\n")).split("\n");
        String title = "Some validations got errors";
        return new ResponseEntity<>(new MultiErrorResponse(title, ErrorType.INVALID_REQUEST, errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleMissingParameter(MissingServletRequestParameterException e) {
        String message = "Required parameter is missing: " + e.getParameterName();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new SingleErrorResponse(message, ErrorType.INVALID_REQUEST));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception e) {
        System.out.println("An unexpected error occurred: " + e.getMessage());
        System.out.println(e.getClass().getName());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new SingleErrorResponse("An unexpected error occurred", ErrorType.UNKNOWN_ERROR));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleConstraintsViolations(DataIntegrityViolationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new SingleErrorResponse("Some data provided are invalid", ErrorType.UNKNOWN_EXISTING_DATA_ERROR));
    }
}

record SingleErrorResponse(String message, ErrorType code) {

}

record MultiErrorResponse(String message, ErrorType code, String[] errors) {

}
