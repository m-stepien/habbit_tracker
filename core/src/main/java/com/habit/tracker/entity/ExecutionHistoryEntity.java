package com.habit.tracker.entity;

import com.habit.tracker.enums.ExecutionState;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class ExecutionHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    @ManyToOne
    HabitEntity habit;
    ExecutionState executionState;
    LocalDate date;

    public ExecutionHistoryEntity() {
    }

    public ExecutionHistoryEntity(HabitEntity habit, ExecutionState executionState, LocalDate date) {
        this.habit = habit;
        this.executionState = executionState;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HabitEntity getHabit() {
        return habit;
    }

    public void setHabit(HabitEntity habit) {
        this.habit = habit;
    }

    public ExecutionState getExecutionState() {
        return executionState;
    }

    public void setExecutionState(ExecutionState executionState) {
        this.executionState = executionState;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
