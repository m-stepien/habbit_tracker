package com.habit.tracker.core.dto;

import com.habit.tracker.core.enums.ExecutionState;

import java.time.LocalDate;

public record ExecutionHistoryDayDto(Long id, LocalDate date, ExecutionState state) {
}
