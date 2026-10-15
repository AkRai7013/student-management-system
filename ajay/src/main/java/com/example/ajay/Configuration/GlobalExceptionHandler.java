package com.example.ajay.Configuration;

import com.example.ajay.Exception.StudentNotFoundException;
import com.example.ajay.response.ResponseGlobal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle student not found
    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<ResponseGlobal<?>> handleStudentNotFound(
            StudentNotFoundException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ResponseGlobal.onError(ex.getMessage()));
    }

    // Handle validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseGlobal<?>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        String errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": "
                        + error.getDefaultMessage())
                .distinct()
                .collect(Collectors.joining("; "));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseGlobal.onError(errors));
    }
}