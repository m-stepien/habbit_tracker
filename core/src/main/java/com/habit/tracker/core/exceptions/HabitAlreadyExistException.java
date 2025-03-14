package com.habit.tracker.core.exceptions;

public class HabitAlreadyExistException extends RuntimeException {
    public HabitAlreadyExistException(String message) {
        super(message);
    }
}
