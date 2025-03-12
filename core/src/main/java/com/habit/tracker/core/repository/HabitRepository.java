package com.habit.tracker.core.repository;

import com.habit.tracker.core.entity.HabitEntity;
import com.habit.tracker.core.enums.HabitStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HabitRepository extends JpaRepository<HabitEntity, Long> {
    Optional<HabitEntity> findByIdAndUserId(long id, String userId);
    List<HabitEntity> findByUserId(String userId);
    List<HabitEntity> findByUserIdAndStatus(String userId, HabitStatus status);
    List<HabitEntity> findByUserIdAndName(String userId, String name);
    List<HabitEntity> findByStatus(HabitStatus status);
}
