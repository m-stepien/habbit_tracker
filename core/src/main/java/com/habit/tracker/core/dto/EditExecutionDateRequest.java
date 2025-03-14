package com.habit.tracker.core.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;
@Schema(description = "Data transfer object used for sending request to change date of execution history record")
public record EditExecutionDateRequest(
        @Schema(example = "3")
        @NotNull(message = "execution id cannot be null")
        Long id,
        @Schema(description = "New date that user want to give to execution history record. Should by in range between current day and some number of days defined in application.yml", example = "2024-02-12")
        @NotNull(message = "Mark date cannot be null")
        @PastOrPresent(message = "Mark date cannot be in the future")
        LocalDate newDate) {
}
