package com.habit.tracker.dto;

import com.habit.tracker.enums.ExecutionDayOption;

import java.time.LocalDate;
import java.util.List;

public record HabitWithDaysDto(Long id, String name,
                               Integer points, LocalDate creationDate, Integer unlockCost,
                               List<ExecutionDayOption> executionDayList) {
}
