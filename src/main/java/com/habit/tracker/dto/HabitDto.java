package com.habit.tracker.dto;

import java.time.LocalDate;

public record HabitDto(Long id, String name,
                       Integer points, LocalDate creationDate, Integer unlockCost){
}
