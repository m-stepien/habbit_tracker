package com.habit.tracker.core.mapper;

import com.habit.tracker.core.dto.HabitCreateRequestDto;
import com.habit.tracker.core.dto.HabitDto;
import com.habit.tracker.core.dto.HabitWithDaysDto;
import com.habit.tracker.core.entity.ExecutionDaysEntity;
import com.habit.tracker.core.entity.HabitEntity;
import org.springframework.stereotype.Component;


@Component
public class HabitMapper {

    public HabitDto toHabitDto(HabitEntity habitEntity) {
        return new HabitDto(habitEntity.getId(), habitEntity.getName(), habitEntity.getPoints(),
                habitEntity.getCreationDate(), habitEntity.getUnlockCost(), habitEntity.getStatus(),
                habitEntity.getDaysToMaster(), habitEntity.getRemainingDays());
    }

    public HabitWithDaysDto habitWithDaysDto(HabitEntity habitEntity, ExecutionDaysEntity executionDays) {
        return new HabitWithDaysDto(habitEntity.getId(), habitEntity.getName(), habitEntity.getPoints(),
                habitEntity.getCreationDate(), habitEntity.getUnlockCost(), habitEntity.getStatus(),
                executionDays.getExecutionDays().stream().toList(), habitEntity.getDaysToMaster(), habitEntity.getRemainingDays());
    }

    public HabitEntity toHabitEntity(HabitDto habitDto) {
        HabitEntity habit = new HabitEntity(habitDto.name(), habitDto.points(),
                habitDto.creationDate(), habitDto.unlockCost());
        habit.setDaysToMaster(habitDto.daysToMaster());
        return habit;
    }

    public HabitDto toHabitDto(HabitCreateRequestDto habitCreateRequestDto){
        return  new HabitDto(habitCreateRequestDto.id(), habitCreateRequestDto.name(), habitCreateRequestDto.points(),
                habitCreateRequestDto.creationDate(), habitCreateRequestDto.unlockCost(), habitCreateRequestDto.status(),
                habitCreateRequestDto.daysToMaster() ,habitCreateRequestDto.daysToMaster());
    }
}
