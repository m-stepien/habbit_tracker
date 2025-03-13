package com.habit.tracker.core.dto;

import com.habit.tracker.core.enums.ExecutionState;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public record MarkHabitRequestDto(
        @NotNull(message = "habit id cannot be null")
        Long habitId,
        @NotBlank(message = "Execution state cannot by empty. Allowed values DONE, NOTDONECHECKED")
        ExecutionState executionState,
        @NotNull(message = "Mark date cannot be null")
        @PastOrPresent(message = "Mark date cannot be in the future")
        LocalDate date) {
}
