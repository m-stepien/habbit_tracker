package com.habit.tracker.core.controller;

import com.habit.tracker.core.dto.EditExecutionDateRequest;
import com.habit.tracker.core.dto.ExecutionHistoryDayDto;
import com.habit.tracker.core.dto.HabitExecutionHistoryDto;
import com.habit.tracker.core.dto.MarkHabitRequestDto;
import com.habit.tracker.core.exceptions.IncorrectDateException;
import com.habit.tracker.core.service.ExecutionHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Return execution history of all habit from provided day")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful return execution history of all habit from provided day If none found return empty list.")
    })
    public ResponseEntity<List<ExecutionHistoryDayDto>> getExecutionInDay(@AuthenticationPrincipal Jwt jwt, @RequestParam("date") LocalDate date){
        String userId = jwt.getClaim("sub");
        logger.info("Get execution history on day {} for user {}", date, userId);
        List<ExecutionHistoryDayDto> executionHistoryDtoList = this.executionHistoryService
                .getExecutionInDay(userId, date);
        return ResponseEntity.ok(executionHistoryDtoList);
    }

    @Operation(summary = "Get all execution history for provided habit")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful return execution history of habit. In no execution history exist return empty list"),
            @ApiResponse(responseCode = "400", description = "User don't own this habit")
    })
    @GetMapping("/by/habit")
    public ResponseEntity<HabitExecutionHistoryDto> getExecutionOfHabit(@AuthenticationPrincipal Jwt jwt, @RequestParam("id") Long id){
        String userId = jwt.getClaim("sub");
        logger.info("Get execution history for habit {} user {}", id, userId);
        HabitExecutionHistoryDto habitExecutionHistory = this.executionHistoryService.getExecutionOfHabit(userId, id);
        return ResponseEntity.ok(habitExecutionHistory);
    }

    @Operation(summary = "Return list of list of execution habit for all habit", description = "Response contains list of element that's contain habit name and list of execution of it.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful return. If nothing found return empty list"),
            @ApiResponse(responseCode = "400", description = "User don't own this habit")
    })
    @GetMapping("/all")
    public ResponseEntity<List<HabitExecutionHistoryDto>> getAllExecutions(@AuthenticationPrincipal Jwt jwt){
        String userId = jwt.getClaim("sub");
        logger.info("Get all execution history for user {}", userId);
        List<HabitExecutionHistoryDto> executionHistoryList = this.executionHistoryService.getAllExecutionHistory(userId);
        return ResponseEntity.ok(executionHistoryList);
    }

    @Operation(summary = "Create execution history for habit", description = "User can mark execution of habit in day. For some number of days defined in application.yml user can also modify execution history elements for example if he mark habit as done but actually wanted to mark it as undone. When modification happen service canceled past effect for example if before state was done service mill rollback remainingDays and user points.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful create or update execution history record"),
            @ApiResponse(responseCode = "400", description = "Habit doesn't exist or is not own be user or is not in active state"),
            @ApiResponse(responseCode = "500", description = "Invalid data in request")
    })
    @PostMapping("/mark")
    public ResponseEntity<Void> markHabitInDay(@AuthenticationPrincipal Jwt jwt,
                                               @RequestBody MarkHabitRequestDto markHabitRequest) throws IncorrectDateException {
        String userId = jwt.getClaim("sub");
        logger.info("Post execution history of habit {} for user {}", markHabitRequest.habitId(), userId);
        this.executionHistoryService.markHabitInDay(userId,
                markHabitRequest.habitId(), markHabitRequest.executionState(), markHabitRequest.date());
        return ResponseEntity.noContent().build();
    }
    @Operation(summary = "Update date of execution history record", description = "To work both date before and after must be in range from current date to number of days back declared in application.yml")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful update of date"),
            @ApiResponse(responseCode = "400", description = "Execution history record with this id doesn't exist or habit is not own by user or one of date is not in range"),
            @ApiResponse(responseCode = "500", description = "Invalid data in request")
    })
    @PostMapping("/edit")
    public ResponseEntity<Void> editExecutionDate(@AuthenticationPrincipal Jwt jwt, @RequestBody EditExecutionDateRequest editExecutionDateRequest) throws IncorrectDateException{
        String userId = jwt.getClaim("sub");
        logger.info("Post update execution history id {} for user {}", editExecutionDateRequest.id(), userId);
        this.executionHistoryService.editDateOfExecution(userId, editExecutionDateRequest.id(), editExecutionDateRequest.newDate());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete execution history record")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful delete")
    })
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteExecution(@AuthenticationPrincipal Jwt jwt, @RequestParam("id") Long id){
        String userId = jwt.getClaim("sub");
        logger.info("Delete execution {} for user {}", userId, id);
        this.executionHistoryService.deleteHabitExecutionInDay(userId, id);
        return ResponseEntity.noContent().build();
    }
}
