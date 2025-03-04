package com.habit.tracker.repository;

import com.habit.tracker.entity.PointsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PointsRepository  extends JpaRepository<PointsEntity, String> {
}
