package com.habit.tracker.core.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record HabitUpdateRequestDto(
        @NotNull(message = "habit id cannot be null")
        Long id,
        String name,
        @Min(value = 0, message = "Points must be at least 0")
        Integer points,
        @Min(value = 0, message = "Unlock cost must be at least 0")
        Integer unlockCost,
        @Min(value = 1, message = "Days to master must be at least 1")
        Integer daysToMaster) {
}
