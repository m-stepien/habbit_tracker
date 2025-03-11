package com.habit.tracker.core.controller;

import com.habit.tracker.core.exceptions.IncorrectDateException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IncorrectDateException.class)
    public ResponseEntity<Map<String, Object>> handleIncorrectDateException(IncorrectDateException exception) {
        Map<String, Object> response = new HashMap<>();
        response.put("Message", exception.getMessage());
        response.put("Error", "Invalid argument");
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(IncorrectDateException exception) {
        Map<String, Object> response = new HashMap<>();
        response.put("Message", exception.getMessage());
        response.put("Error", "Invalid argument");
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception exception) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Internal Server Error");
        response.put("message", exception.getMessage());
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.internalServerError().body(response);
    }

}

