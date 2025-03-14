package com.habit.tracker.core.controller;

import com.habit.tracker.core.exceptions.HabitNotFoundException;
import com.habit.tracker.core.exceptions.IncorrectDateException;
import com.habit.tracker.core.repository.HabitAlreadyExistException;
import jakarta.persistence.EntityNotFoundException;
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

    @ExceptionHandler(HabitNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleHabitNotFoundException(HabitNotFoundException exception){
        Map<String, Object> response = new HashMap<>();
        response.put("Message", exception.getMessage());
        response.put("Error", "Data not found");
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(HabitAlreadyExistException.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(HabitAlreadyExistException exception) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Data already exist");
        response.put("message", exception.getMessage());
        return ResponseEntity.internalServerError().body(response);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEntityNotFoundException(EntityNotFoundException exception){
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Data already exist");
        response.put("message", exception.getMessage());
        return ResponseEntity.internalServerError().body(response);
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

