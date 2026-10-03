package com.example.ajay.Configuration;

import com.example.ajay.Exception.StudentNotFoundException;
import com.example.ajay.response.ResponseGlobal;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseGlobal<?> handleStudentNotFoundException(
            StudentNotFoundException ex) {

        return ResponseGlobal.onError(ex.getMessage());
    }
}