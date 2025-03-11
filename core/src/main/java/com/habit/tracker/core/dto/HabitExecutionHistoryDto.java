package com.habit.tracker.core.dto;

import java.util.List;

public record HabitExecutionHistoryDto(String name, List<ExecutionHistoryDayDto> executionDays) {
}
