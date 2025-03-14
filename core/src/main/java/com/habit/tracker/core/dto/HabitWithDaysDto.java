package com.habit.tracker.core.dto;

import com.habit.tracker.core.enums.ExecutionDayOption;
import com.habit.tracker.core.enums.HabitStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "Data transfer object for returning habit without days to execute")
public record HabitWithDaysDto(
        @Schema(example = "3")
        Long id,
        @Schema(description = "Description of habit that user want to master", example = "Make bed")
        String name,
        @Schema(description = "Reward for executing habit. Used also for punishment if user doesn't execute habit or forget to mark", example = "20")
        Integer points,
        @Schema(example = "2024-03-11")
        LocalDate creationDate,
        @Schema(description = "Cost of unlocking habit. User must pay points to unlock habit.", example = "340")
        Integer unlockCost,
        @Schema(description = "State of habit if habit is in inactive state it's mean user create but doesn't bought this habit. State active means that user bought habit and can execute it. State complete means that user mastered this habit and remaining day is 0", example = "ACTIVE")
        HabitStatus status,
        @Schema(description = "Days of week in which user want to execute habit", example="[\"MONDAY\", \"SUNDAY\"]")
        List<ExecutionDayOption> executionDayList,
        @Schema(description = "Number of days that will take to master habit meaning how much time user should repeat habit to do it automatically without forcing themself. Chose be user.", example = "40")
        Integer daysToMaster,
        @Schema(description = "Remaining days to master habit. Initial value is equal to daysToMaster then it's reducing when user execute habit", example = "29")
        Integer remainingDays) {
}
