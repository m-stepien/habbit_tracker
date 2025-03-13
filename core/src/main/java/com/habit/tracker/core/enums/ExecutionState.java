package com.habit.tracker.core.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ExecutionState {
    DONE,
    NOTDONECHECKED,
    NOTDONEUNCHECKED;

    @JsonCreator
    public static ExecutionState fromString(String value){
        return ExecutionState.valueOf(value.toUpperCase());
    }
}
