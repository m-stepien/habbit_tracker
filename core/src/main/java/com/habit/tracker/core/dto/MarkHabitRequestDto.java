package com.habit.tracker.core.dto;

import com.habit.tracker.core.enums.ExecutionState;

import java.time.LocalDate;

public record MarkHabitRequestDto(Long habitId, ExecutionState executionState, LocalDate date) {
}
