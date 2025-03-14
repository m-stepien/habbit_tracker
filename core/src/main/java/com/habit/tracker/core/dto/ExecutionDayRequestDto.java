package com.habit.tracker.core.dto;

import com.habit.tracker.core.enums.ExecutionDayOption;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;
@Schema(description = "Data transfer object to add preferred days to execute habit")
public record ExecutionDayRequestDto (
        @Schema(description = "Habit id to modify", example = "3")
        @NotNull(message = "Habit ID cannot be null")
        @Positive(message = "Habit ID must be a positive number")
        Long habitId,
        @Schema(description = "Days of week in which user want to execute habit", example="[\"MONDAY\", \"SUNDAY\"]")
        @NotEmpty(message = "Execution day list cannot be empty")
        List<ExecutionDayOption> executionDayList){
}
