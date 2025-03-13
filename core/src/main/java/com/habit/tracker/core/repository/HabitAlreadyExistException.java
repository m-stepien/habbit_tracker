package com.habit.tracker.core.repository;

public class HabitAlreadyExistException extends RuntimeException {
    public HabitAlreadyExistException(String message) {
        super(message);
    }
}
