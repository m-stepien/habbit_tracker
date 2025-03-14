package com.habit.tracker.core.dto;

import com.habit.tracker.core.enums.ExecutionState;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
@Schema(description = "Data transfer object used to return execution history for specific date")
public record ExecutionHistoryDayDto(
        @Schema(example = "3")
        Long id,
        @Schema(description = "Habit that was executed")
        HabitDto habit,
        @Schema(description = "Provide information was habit done or undone and did user mark it himself", example = "UNDONEUNCHECKED")
        ExecutionState executionState,
        @Schema(description = "Date of execution", example = "2024-12-12")
        LocalDate date) {

}
