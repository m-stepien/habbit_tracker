package com.habit.tracker.core.controller;

import com.habit.tracker.core.dto.EditExecutionDateRequest;
import com.habit.tracker.core.dto.ExecutionHistoryDayDto;
import com.habit.tracker.core.dto.HabitExecutionHistoryDto;
import com.habit.tracker.core.dto.MarkHabitRequestDto;
import com.habit.tracker.core.exceptions.IncorrectDateException;
import com.habit.tracker.core.service.ExecutionHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/history/execution")
public class ExecutionHistoryController {

    private ExecutionHistoryService executionHistoryService;

    @Autowired
    public ExecutionHistoryController(ExecutionHistoryService executionHistoryService){
        this.executionHistoryService = executionHistoryService;
    }


    @GetMapping("/in/day")
    public ResponseEntity<List<ExecutionHistoryDayDto>> getExecutionInDay(@AuthenticationPrincipal Jwt jwt, @RequestParam LocalDate date){
        List<ExecutionHistoryDayDto> executionHistoryDtoList = this.executionHistoryService
                .getExecutionInDay(jwt.getClaim("sub"), date);
        return ResponseEntity.ok(executionHistoryDtoList);
    }

    @GetMapping("/by/habit")
    public ResponseEntity<HabitExecutionHistoryDto> getExecutionOfHabit(@AuthenticationPrincipal Jwt jwt, @RequestParam Long id){
        HabitExecutionHistoryDto habitExecutionHistory = this.executionHistoryService.getExecutionOfHabit(jwt.getClaim("sub"), id);
        return ResponseEntity.ok(habitExecutionHistory);
    }

    @GetMapping("/all")
    public ResponseEntity<List<HabitExecutionHistoryDto>> getAllExecutions(@AuthenticationPrincipal Jwt jwt){
        List<HabitExecutionHistoryDto> executionHistoryList = this.executionHistoryService.getAllExecutionHistory(jwt.getClaim("sub"));
        return ResponseEntity.ok(executionHistoryList);
    }

    @PostMapping("/mark")
    public ResponseEntity<Void> markHabitInDay(@AuthenticationPrincipal Jwt jwt,
                                               @RequestBody MarkHabitRequestDto markHabitRequest) throws IncorrectDateException {
        this.executionHistoryService.markHabitInDay(jwt.getClaim("sub"),
                markHabitRequest.habitId(), markHabitRequest.executionState(), markHabitRequest.date());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/edit")
    public ResponseEntity<Void> editExecutionDate(@AuthenticationPrincipal Jwt jwt, @RequestBody EditExecutionDateRequest editExecutionDateRequest) throws IncorrectDateException{
        this.executionHistoryService.editDateOfExecution(jwt.getClaim("sub"), editExecutionDateRequest.id(), editExecutionDateRequest.newDate());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/delete")
    public ResponseEntity<Void> deleteExecution(@AuthenticationPrincipal Jwt jwt, Long id){
        this.executionHistoryService.deleteHabitExecutionInDay(jwt.getClaim("sub"), id);
        return ResponseEntity.noContent().build();
    }
}
