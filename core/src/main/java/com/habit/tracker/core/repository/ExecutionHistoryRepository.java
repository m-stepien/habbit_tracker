package com.habit.tracker.core.repository;

import com.habit.tracker.core.entity.ExecutionHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExecutionHistoryRepository extends JpaRepository<ExecutionHistoryEntity, Long> {
    @Query("SELECT e FROM ExecutionHistoryEntity e WHERE e.habit.id = :habit_id AND e.date = :date ORDER BY id ASC LIMIT 1")
    public Optional<ExecutionHistoryEntity> findByHabitIdAndDate(@Param("habit_id") Long id, @Param("date") LocalDate localDate);

    @Query("SELECT e FROM ExecutionHistoryEntity e WHERE e.habit.userId = :userId AND e.date = :date")
    public List<ExecutionHistoryEntity> findExecutionInDay(@Param("userId") String userId, @Param("date") LocalDate date);

    @Query("SELECT e FROM ExecutionHistoryEntity e WHERE e.habit.id = :habitId")
    public List<ExecutionHistoryEntity> findExecutionForHabitId(@Param("habitId") Long habitId);
}
