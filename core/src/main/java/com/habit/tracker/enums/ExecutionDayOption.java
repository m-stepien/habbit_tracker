package com.habit.tracker.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

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
}
