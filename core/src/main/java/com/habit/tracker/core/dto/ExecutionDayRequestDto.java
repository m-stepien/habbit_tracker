package com.habit.tracker.core.dto;

import com.habit.tracker.core.enums.ExecutionDayOption;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record ExecutionDayRequestDto (
        @NotNull(message = "Habit ID cannot be null")
        @Positive(message = "Habit ID must be a positive number")
        Long habitId,
        @NotEmpty(message = "Execution day list cannot be empty")
        List<ExecutionDayOption> executionDayList){
}
