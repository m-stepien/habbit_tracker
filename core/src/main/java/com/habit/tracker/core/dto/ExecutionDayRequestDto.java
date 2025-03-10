package com.habit.tracker.core.dto;

import com.habit.tracker.core.enums.ExecutionDayOption;

import java.util.List;

public record ExecutionDayRequestDto (Long habitId, List<ExecutionDayOption> executionDayList){
}
