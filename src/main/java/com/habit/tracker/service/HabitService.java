package com.habit.tracker.service;

import com.habit.tracker.dto.HabitDto;
import com.habit.tracker.dto.HabitWithDaysDto;
import com.habit.tracker.entity.ExecutionDaysEntity;
import com.habit.tracker.entity.HabitEntity;
import com.habit.tracker.enums.ExecutionDayOption;
import com.habit.tracker.enums.HabitStatus;
import com.habit.tracker.mapper.HabitMapper;
import com.habit.tracker.repository.ExecutionDayRepository;
import com.habit.tracker.repository.HabitRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class HabitService {
    private final HabitRepository habitRepository;
    private final HabitMapper habitMapper;
    private final PointsService pointsService;
    private final ExecutionDayRepository executionDayRepository;

    @Autowired
    HabitService(PointsService pointsService, HabitRepository habitRepository,
                 ExecutionDayRepository executionDayRepository, HabitMapper habitMapper) {
        this.pointsService = pointsService;
        this.habitRepository = habitRepository;
        this.executionDayRepository = executionDayRepository;
        this.habitMapper = habitMapper;
    }

    public HabitWithDaysDto getUserHabitById(String userId, long habitId) {
        HabitEntity habit = this.habitRepository.findByIdAndUserId(habitId, userId)
                .orElseThrow(EntityNotFoundException::new);
        ExecutionDaysEntity executionDays = this.executionDayRepository.findByHabit(habit).orElse(new ExecutionDaysEntity());
        return this.habitMapper.habitWithDaysDto(habit, executionDays);
    }

    public List<HabitDto> getUserHabits(String userId) {
        List<HabitEntity> userHabits = this.habitRepository.findByUserId(userId);
        return userHabits.stream().map(this.habitMapper::toHabitDto).toList();
    }

    public void saveUserHabit(String userId, HabitDto newHabitDto) {
        //todo  validation
        //todo check if already exist in repo
        HabitEntity newHabitEntity = this.habitMapper.toHabitEntity(newHabitDto);
        newHabitEntity.setUserId(userId);
        this.habitRepository.save(newHabitEntity);
    }

    @Transactional
    public void purchaseHabit(String userId, Long habitId) {
        HabitEntity habitEntity = this.habitRepository.findByIdAndUserId(habitId, userId)
                .orElseThrow(EntityNotFoundException::new);
        int unlockCost = habitEntity.getUnlockCost();
        if (this.pointsService.canUserBuy(userId, unlockCost)) {
            habitEntity.setStatus(HabitStatus.ACTIVE);
            habitEntity.setPurchaseDate(LocalDate.now());
            this.habitRepository.save(habitEntity);
            this.pointsService.pay(userId, unlockCost);
        }
    }

    public void setHabitExecutionDays(String userId, Long habitId, List<ExecutionDayOption> executionDayOption) {
        if(executionDayOption.contains(ExecutionDayOption.EVERYDAY)){
            executionDayOption = List.of(ExecutionDayOption.EVERYDAY);
        }
        HabitEntity habit = this.habitRepository.findByIdAndUserId(habitId, userId)
                .orElseThrow(EntityNotFoundException::new);
        ExecutionDaysEntity currentExecutionDays = this.executionDayRepository
                .findByHabit(habit).orElseGet(() -> new ExecutionDaysEntity(habit));
        currentExecutionDays.setExecutionDays(new HashSet<>(executionDayOption));
        this.executionDayRepository.save(currentExecutionDays);
    }

    public List<HabitDto> fetchActiveHabitInDay(String userId, ExecutionDayOption day) {
        List<HabitEntity> habitForDay = this.executionDayRepository
                .findForUserForDay(userId, day, ExecutionDayOption.EVERYDAY).stream()
                .map(ExecutionDaysEntity::getHabit).toList();
        habitForDay = habitForDay.stream().filter(habit -> habit.getStatus().equals(HabitStatus.ACTIVE))
                .toList();
        List<HabitDto> habitDtoList = new ArrayList<>();
        for (HabitEntity habit : habitForDay) {
            habitDtoList.add(this.habitMapper.toHabitDto(habit));
        }
        return habitDtoList;
    }

    public HabitEntity getHabitById(Long habitId) {
        return this.habitRepository.findById(habitId).orElseThrow(EntityNotFoundException::new);
    }
}
