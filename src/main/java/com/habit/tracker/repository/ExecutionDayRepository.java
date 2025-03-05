package com.habit.tracker.repository;

import com.habit.tracker.entity.ExecutionDaysEntity;
import com.habit.tracker.enums.ExecutionDayOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExecutionDayRepository extends JpaRepository<ExecutionDaysEntity, Long> {
    @Query("SELECT e FROM ExecutionDaysEntity e WHERE e.habit.user_id = :userId AND (:day IN e.executionDays OR 'ALL' IN e.executionDays)")
    public List<ExecutionDaysEntity> findForUserForDay(String userId, ExecutionDayOption day);

}
