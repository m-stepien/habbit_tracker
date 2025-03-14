package com.habit.tracker.core.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Data transfer object for updating inactive habit")
public record HabitUpdateRequestDto(
        @Schema(example = "3")
        @NotNull(message = "habit id cannot be null")
        Long id,
        @Schema(description = "Description of habit that user want to master", example = "Make bed")
        String name,
        @Schema(description = "Reward for executing habit. Used also for punishment if user doesn't execute habit or forget to mark", example = "20")
        @Min(value = 0, message = "Points must be at least 0")
        Integer points,
        @Schema(description = "Cost of unlocking habit. User must pay points to unlock habit.", example = "340")
        @Min(value = 0, message = "Unlock cost must be at least 0")
        Integer unlockCost,
        @Schema(description = "Number of days that will take to master habit meaning how much time user should repeat habit to do it automatically without forcing themself. Chose be user.", example = "40")
        @Min(value = 1, message = "Days to master must be at least 1")
        Integer daysToMaster) {
}
