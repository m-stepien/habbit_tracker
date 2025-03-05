package com.habit.tracker.entity;

import com.habit.tracker.enums.ExecutionDayOption;
import jakarta.persistence.*;

import java.util.Set;

@Entity
public class ExecutionDaysEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name="habit_id", nullable=false)
    private HabitEntity habit;
    @ElementCollection(targetClass = ExecutionDayOption.class)
    @Enumerated(EnumType.STRING)
    private Set<ExecutionDayOption> executionDays;

    public ExecutionDaysEntity() {
    }

    public ExecutionDaysEntity(HabitEntity habit, Set<ExecutionDayOption> executionDays) {
        this.habit = habit;
        this.executionDays = executionDays;
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

    public Set<ExecutionDayOption> getExecutionDays() {
        return executionDays;
    }

    public void setExecutionDays(Set<ExecutionDayOption> executionDays) {
        this.executionDays = executionDays;
    }
}
