package com.habit.tracker.dto;

import com.habit.tracker.enums.HabitStatus;

import java.time.LocalDate;

public record HabitDto(Long id, String name,
                       Integer points, LocalDate creationDate, Integer unlockCost, HabitStatus status){
}
