package com.habit.tracker.core.exceptions;

public class HabitNotFoundException extends RuntimeException {
    public HabitNotFoundException(long habitId) {
        super("Habit with id "+ habitId + " doesn't exist or is not own by user");
    }

    public static class HabitAlreadyExistException extends RuntimeException {
        public HabitAlreadyExistException(String message) {
            super(message);
        }
    }
}
