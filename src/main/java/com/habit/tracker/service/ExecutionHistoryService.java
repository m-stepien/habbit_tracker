package com.habit.tracker.service;

import com.habit.tracker.entity.HabitEntity;
import com.habit.tracker.enums.HabitStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class ExecutionHistoryService {
    private HabitService habitService;

    @Autowired
    public ExecutionHistoryService(HabitService habitService) {
        this.habitService = habitService;
    }

    public void markHabitInDay(String userId, Long habitId) {
        HabitEntity habit = this.habitService.getHabitById(habitId);
        if (userId.equals(habit.getUserId()) && habit.getStatus().equals(HabitStatus.ACTIVE)) {
            //todo continue here
        } else if (habit.getStatus().equals(HabitStatus.ACTIVE)) {
            //propably use custrom exception
            throw new AccessDeniedException("Habit is not purchase");
        } else {
            throw new AccessDeniedException("You don't own this habit");
        }
    }
}
