package com.habit.tracker.core.controller;

import com.habit.tracker.core.dto.EditExecutionDateRequest;
import com.habit.tracker.core.dto.ExecutionHistoryDayDto;
import com.habit.tracker.core.dto.HabitExecutionHistoryDto;
import com.habit.tracker.core.dto.MarkHabitRequestDto;
import com.habit.tracker.core.exceptions.IncorrectDateException;
import com.habit.tracker.core.service.ExecutionHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(ExecutionHistoryController.class);


    @Autowired
    public ExecutionHistoryController(ExecutionHistoryService executionHistoryService){
        this.executionHistoryService = executionHistoryService;
    }


    @GetMapping("/in/day")
    public ResponseEntity<List<ExecutionHistoryDayDto>> getExecutionInDay(@AuthenticationPrincipal Jwt jwt, @RequestParam("date") LocalDate date){
        String userId = jwt.getClaim("sub");
        logger.info("Get execution history on day {} for user {}", date, userId);
        List<ExecutionHistoryDayDto> executionHistoryDtoList = this.executionHistoryService
                .getExecutionInDay(userId, date);
        return ResponseEntity.ok(executionHistoryDtoList);
    }

    @GetMapping("/by/habit")
    public ResponseEntity<HabitExecutionHistoryDto> getExecutionOfHabit(@AuthenticationPrincipal Jwt jwt, @RequestParam("id") Long id){
        String userId = jwt.getClaim("sub");
        logger.info("Get execution history for habit {} user {}", id, userId);
        HabitExecutionHistoryDto habitExecutionHistory = this.executionHistoryService.getExecutionOfHabit(userId, id);
        return ResponseEntity.ok(habitExecutionHistory);
    }

    @GetMapping("/all")
    public ResponseEntity<List<HabitExecutionHistoryDto>> getAllExecutions(@AuthenticationPrincipal Jwt jwt){
        String userId = jwt.getClaim("sub");
        logger.info("Get all execution history for user {}", userId);
        List<HabitExecutionHistoryDto> executionHistoryList = this.executionHistoryService.getAllExecutionHistory(userId);
        return ResponseEntity.ok(executionHistoryList);
    }

    @PostMapping("/mark")
    public ResponseEntity<Void> markHabitInDay(@AuthenticationPrincipal Jwt jwt,
                                               @RequestBody MarkHabitRequestDto markHabitRequest) throws IncorrectDateException {
        String userId = jwt.getClaim("sub");
        logger.info("Post execution history of habit {} for user {}", markHabitRequest.habitId(), userId);
        this.executionHistoryService.markHabitInDay(userId,
                markHabitRequest.habitId(), markHabitRequest.executionState(), markHabitRequest.date());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/edit")
    public ResponseEntity<Void> editExecutionDate(@AuthenticationPrincipal Jwt jwt, @RequestBody EditExecutionDateRequest editExecutionDateRequest) throws IncorrectDateException{
        String userId = jwt.getClaim("sub");
        logger.info("Post update execution history id {} for user {}", editExecutionDateRequest.id(), userId);
        this.executionHistoryService.editDateOfExecution(userId, editExecutionDateRequest.id(), editExecutionDateRequest.newDate());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteExecution(@AuthenticationPrincipal Jwt jwt, @RequestParam("id") Long id){
        String userId = jwt.getClaim("sub");
        logger.info("Delete execution {} for user {}", userId, id);
        this.executionHistoryService.deleteHabitExecutionInDay(userId, id);
        return ResponseEntity.noContent().build();
    }
}
