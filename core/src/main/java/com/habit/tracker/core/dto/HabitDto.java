package com.habit.tracker.core.dto;

import com.habit.tracker.core.enums.HabitStatus;

import java.time.LocalDate;

public record HabitDto(Long id, String name,
                       Integer points, LocalDate creationDate, Integer unlockCost, HabitStatus status){
}
