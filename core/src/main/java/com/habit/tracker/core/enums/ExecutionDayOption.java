package com.habit.tracker.core.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.time.DayOfWeek;

public enum ExecutionDayOption {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY,
    EVERYDAY;

    @JsonCreator
    public static ExecutionDayOption fromString(String value){
        return ExecutionDayOption.valueOf(value.toUpperCase());
    }

    public static ExecutionDayOption fromDayOfWeek(DayOfWeek dayOfWeek) {
        return ExecutionDayOption.valueOf(dayOfWeek.name());
    }
}
