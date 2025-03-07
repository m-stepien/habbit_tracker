package com.habit.tracker.repository;

import com.habit.tracker.entity.HabitEntity;
import com.habit.tracker.enums.HabitStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HabitRepository extends JpaRepository<HabitEntity, Long> {
    Optional<HabitEntity> findByIdAndUserId(long id, String userId);
    List<HabitEntity> findByUserId(String userId);
    List<HabitEntity> findByUserIdAndStatus(String userId, HabitStatus status);
}
