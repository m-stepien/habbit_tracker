package com.habit.tracker.core.exceptions;

public class ExecutionHistoryNotFoundException extends RuntimeException {
    public ExecutionHistoryNotFoundException(Long id) {
        super("Habit with id " + id + " doesn't exist or is not own by user"
        );
    }
}
