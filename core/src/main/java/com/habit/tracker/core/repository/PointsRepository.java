package com.habit.tracker.core.repository;

import com.habit.tracker.core.entity.PointsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PointsRepository  extends JpaRepository<PointsEntity, String> {
}
