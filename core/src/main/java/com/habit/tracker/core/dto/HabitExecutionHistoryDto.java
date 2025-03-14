package com.habit.tracker.core.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
@Schema(description = "Data transfer object for returning habit name with execution history")
public record HabitExecutionHistoryDto(
        @Schema(description = "Name of habit", example = "Run")
        String name,
        @Schema(description = "List of execution history show when habit was done or not done")
        List<ExecutionHistoryDto> executionDays) {
}
