package com.habit.tracker.repository;

import com.habit.tracker.entity.ExecutionDaysEntity;
import com.habit.tracker.entity.HabitEntity;
import com.habit.tracker.enums.ExecutionDayOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExecutionDayRepository extends JpaRepository<ExecutionDaysEntity, Long> {
    @Query("SELECT e FROM ExecutionDaysEntity e WHERE e.habit.userId = :userId AND" +
            " (:day MEMBER OF e.executionDays OR :all MEMBER OF e.executionDays)")
    public List<ExecutionDaysEntity> findForUserForDay(@Param("userId") String userId, @Param("day") ExecutionDayOption day, @Param("all") ExecutionDayOption everyday);
    public Optional<ExecutionDaysEntity> findByHabit(HabitEntity habit);
}
