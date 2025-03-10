package com.habit.tracker.core.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class PointsEntity{
    @Id
    String userId;
    Integer points;

    public PointsEntity(){
    }

    public PointsEntity(String userId, Integer points){
        this.userId = userId;
        this.points = points;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }
}
