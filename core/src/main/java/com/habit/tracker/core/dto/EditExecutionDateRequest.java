package com.habit.tracker.core.dto;

import java.time.LocalDate;

public record EditExecutionDateRequest(Long id, LocalDate newDate) {
}
