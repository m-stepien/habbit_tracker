package com.habit.tracker.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class ExecutionHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    @ManyToOne
    HabitEntity habit;
    LocalDate date;
}
