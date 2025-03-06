package com.habit.tracker.service;

import com.habit.tracker.entity.ExecutionHistoryEntity;
import com.habit.tracker.entity.HabitEntity;
import com.habit.tracker.enums.ExecutionState;
import com.habit.tracker.enums.HabitStatus;
import com.habit.tracker.repository.ExecutionHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ExecutionHistoryService {
    private HabitService habitService;
    private ExecutionHistoryRepository executionHistoryRepository;

    @Autowired
    public ExecutionHistoryService(HabitService habitService, ExecutionHistoryRepository executionHistoryRepository) {
        this.habitService = habitService;
        this.executionHistoryRepository = executionHistoryRepository;
    }

    public void markHabitInDay(String userId, Long habitId, ExecutionState state, LocalDate date) {
        HabitEntity habit = this.habitService.getHabitById(habitId);
        if (userId.equals(habit.getUserId()) && habit.getStatus().equals(HabitStatus.ACTIVE)) {
            if(date==null){
                date = LocalDate.now();
            }
            this.executionHistoryRepository.save(new ExecutionHistoryEntity(habit, state, date));
        } else if (habit.getStatus().equals(HabitStatus.ACTIVE)) {
              throw new AccessDeniedException("Habit is not purchase");
        } else {
            throw new AccessDeniedException("You don't own this habit");
        }
    }
}
