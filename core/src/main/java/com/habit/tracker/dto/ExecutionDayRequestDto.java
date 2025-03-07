package com.habit.tracker.dto;

import com.habit.tracker.enums.ExecutionDayOption;

import java.util.List;

public record ExecutionDayRequestDto (Long habitId, List<ExecutionDayOption> executionDayList){
}
