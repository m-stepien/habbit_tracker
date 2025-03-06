package com.habit.tracker.mapper;

import com.habit.tracker.dto.HabitDto;
import com.habit.tracker.dto.HabitWithDaysDto;
import com.habit.tracker.entity.ExecutionDaysEntity;
import com.habit.tracker.entity.HabitEntity;
import org.springframework.stereotype.Component;


@Component
public class HabitMapper {

    public HabitDto toHabitDto(HabitEntity habitEntity) {
        return new HabitDto(habitEntity.getId(), habitEntity.getName(), habitEntity.getPoints(), habitEntity.getCreationDate(),
                habitEntity.getUnlockCost());
    }

    public HabitWithDaysDto habitWithDaysDto(HabitEntity habitEntity, ExecutionDaysEntity executionDays) {
        return new HabitWithDaysDto(habitEntity.getId(), habitEntity.getName(), habitEntity.getPoints(), habitEntity.getCreationDate(),
                habitEntity.getUnlockCost(), executionDays.getExecutionDays().stream().toList());
    }

    public HabitEntity toHabitEntity(HabitDto habitDto) {
        return new HabitEntity(habitDto.name(), habitDto.points(), habitDto.creationDate(), habitDto.unlockCost());
    }
}
