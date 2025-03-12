package com.habit.tracker.core.exceptions;

public class HabitAlreadyBoughtException extends RuntimeException {
    public HabitAlreadyBoughtException(String message) {
        super(message);
    }
}
