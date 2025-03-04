package com.habit.tracker.service;

import com.habit.tracker.dto.HabitDto;
import com.habit.tracker.entity.HabitEntity;
import com.habit.tracker.enums.HabitStatus;
import com.habit.tracker.mapper.HabitMapper;
import com.habit.tracker.repository.HabitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HabitService {
    private final HabitRepository habitRepository;
    private final HabitMapper habitMapper;
    private final PointsService pointsService;

    @Autowired
    HabitService(PointsService pointsService, HabitRepository habitRepository, HabitMapper habitMapper){
        this.pointsService = pointsService;
        this.habitRepository = habitRepository;
        this.habitMapper = habitMapper;
    }

    public HabitDto getUserHabitByHabitId(String userId, long habitId){
        HabitEntity habit = this.habitRepository.findByIdAndUserId(habitId, userId);
        return this.habitMapper.toHabitDto(habit);
    }

    public List<HabitDto> getUserHabits(String userId){
        List<HabitEntity> userHabits = this.habitRepository.findByUserId(userId);
        return userHabits.stream().map(this.habitMapper::toHabitDto).toList();
    }

    public void saveUserHabit(String userId, HabitDto newHabitDto){
        //todo  validation
        //todo check if already exist in repo
        HabitEntity newHabitEntity = this.habitMapper.toHabitEntity(newHabitDto);
        newHabitEntity.setUserId(userId);
        this.habitRepository.save(newHabitEntity);
    }

    @Transactional
    public void purchaseHabit(String userId, Long habitId){
        HabitEntity habitEntity = this.habitRepository.findByIdAndUserId(habitId, userId);
        int unlockCost = habitEntity.getUnlockCost();
        if(this.pointsService.canUserBuy(userId, unlockCost)){
            habitEntity.setStatus(HabitStatus.ACTIVE);
            this.pointsService.pay(userId, unlockCost);
        }
    }

}
