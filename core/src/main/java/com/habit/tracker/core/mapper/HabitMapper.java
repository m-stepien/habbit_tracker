package com.habit.tracker.core.mapper;

import com.habit.tracker.core.dto.HabitDto;
import com.habit.tracker.core.dto.HabitWithDaysDto;
import com.habit.tracker.core.entity.ExecutionDaysEntity;
import com.habit.tracker.core.entity.HabitEntity;
import org.springframework.stereotype.Component;


@Component
public class HabitMapper {

    public HabitDto toHabitDto(HabitEntity habitEntity) {
        return new HabitDto(habitEntity.getId(), habitEntity.getName(), habitEntity.getPoints(),
                habitEntity.getCreationDate(), habitEntity.getUnlockCost(), habitEntity.getStatus());
    }

    public HabitWithDaysDto habitWithDaysDto(HabitEntity habitEntity, ExecutionDaysEntity executionDays) {
        return new HabitWithDaysDto(habitEntity.getId(), habitEntity.getName(), habitEntity.getPoints(),
                habitEntity.getCreationDate(), habitEntity.getUnlockCost(), habitEntity.getStatus(),
                executionDays.getExecutionDays().stream().toList());
    }

    public HabitEntity toHabitEntity(HabitDto habitDto) {
        return new HabitEntity(habitDto.name(), habitDto.points(),
                habitDto.creationDate(), habitDto.unlockCost());
    }
}
