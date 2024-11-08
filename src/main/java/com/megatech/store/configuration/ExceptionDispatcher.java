package com.megatech.store.configuration;

import com.megatech.store.exceptions.EntityNotFoundException;
import com.megatech.store.exceptions.FieldConstraintViolationException;
import com.megatech.store.exceptions.InvalidProductFieldException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class ExceptionDispatcher {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFoundException(EntityNotFoundException e) {
        return new ResponseEntity<>(new SingleErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FieldConstraintViolationException.class)
    public ResponseEntity<?> handleFieldConstraintViolationException(FieldConstraintViolationException e) {
        return new ResponseEntity<>(new SingleErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidProductFieldException.class)
    public ResponseEntity<?> handleInvalidProductFieldException(InvalidProductFieldException e) {
        String prefix = "Failed operation in product: ";
        return new ResponseEntity<>(new SingleErrorResponse(prefix + e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationError(MethodArgumentNotValidException e) {
        String[] errors = e.getFieldErrors().stream().map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("\n")).split("\n");
        String title = "Some validations got errors";
        return new ResponseEntity<>(new MultiErrorResponse(title, errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleConstraintsViolations(DataIntegrityViolationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new SingleErrorResponse("Some data provided are invalid"));
    }
}

record SingleErrorResponse(String message) {

}

record MultiErrorResponse(String message, String[] errors) {

}
