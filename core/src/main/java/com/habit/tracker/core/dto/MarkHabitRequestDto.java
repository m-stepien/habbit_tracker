package com.habit.tracker.core.dto;

import com.habit.tracker.core.enums.ExecutionState;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;
@Schema(description = "Data transfer object to create execution history record for habit")
public record MarkHabitRequestDto(
        @Schema(example = "3")
        @NotNull(message = "habit id cannot be null")
        Long habitId,
        @Schema(description = "Tell if user done habit", example = "DONE")
        @NotBlank(message = "Execution state cannot by empty. Allowed values DONE, NOTDONECHECKED")
        ExecutionState executionState,
        @Schema(description = "Date of execution", example = "2024-12-11")
        @NotNull(message = "Mark date cannot be null")
        @PastOrPresent(message = "Mark date cannot be in the future")
        LocalDate date) {
}
