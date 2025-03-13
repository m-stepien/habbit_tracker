package com.habit.tracker.core.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public record EditExecutionDateRequest(
        @NotNull(message = "execution id cannot be null")
        Long id,
        @NotNull(message = "Mark date cannot be null")
        @PastOrPresent(message = "Mark date cannot be in the future")
        LocalDate newDate) {
}
