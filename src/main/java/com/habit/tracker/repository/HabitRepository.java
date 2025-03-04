package com.habit.tracker.repository;

import com.habit.tracker.entity.HabitEntity;
import com.habit.tracker.enums.HabitStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HabitRepository extends JpaRepository<HabitEntity, Long> {
    HabitEntity findByIdAndUserId(long id, String userId);
    List<HabitEntity> findByUserId(String userId);
    List<HabitEntity> findByUserIdAndStatus(String userId, HabitStatus status);
}
