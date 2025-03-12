package com.habit.tracker.core.dto;

import com.habit.tracker.core.enums.ExecutionState;

import java.time.LocalDate;

public record ExecutionHistoryDto(Long id, LocalDate date, ExecutionState state) {
}
