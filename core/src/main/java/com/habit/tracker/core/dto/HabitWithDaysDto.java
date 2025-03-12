package com.habit.tracker.core.dto;

import com.habit.tracker.core.enums.ExecutionDayOption;
import com.habit.tracker.core.enums.HabitStatus;

import java.time.LocalDate;
import java.util.List;

public record HabitWithDaysDto(Long id, String name,
                               Integer points, LocalDate creationDate,
                               Integer unlockCost, HabitStatus status,
                               List<ExecutionDayOption> executionDayList,
                               Integer daysToMaster, Integer remainingDays) {
}
