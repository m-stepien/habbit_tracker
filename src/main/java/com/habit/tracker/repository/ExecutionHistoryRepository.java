package com.habit.tracker.repository;

import com.habit.tracker.entity.ExecutionDaysEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExecutionHistoryRepository extends JpaRepository<ExecutionDaysEntity, Long> {
}
