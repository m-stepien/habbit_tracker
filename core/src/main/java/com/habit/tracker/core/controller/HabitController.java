package com.habit.tracker.core.controller;

import com.habit.tracker.core.dto.*;
import com.habit.tracker.core.mapper.HabitMapper;
import com.habit.tracker.core.service.HabitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/habit")
public class HabitController {
    private final HabitService habitService;
    private final HabitMapper habitMapper;
    private static final Logger logger = LoggerFactory.getLogger(HabitController.class);

    @Autowired
    HabitController(HabitService habitService, HabitMapper habitMapper) {
        this.habitService = habitService;
        this.habitMapper = habitMapper;
    }

    @Operation(summary = "Returning all habit related to user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Returning all habit related to user or empty list if none exist")
    })
    @GetMapping("/get/all")
    public ResponseEntity<List<HabitDto>> getUserHabitsList(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getClaimAsString("sub");
        logger.info("Start get all habit of user {}", userId);
        List<HabitDto> habitDtoList = this.habitService.getUserHabits(userId);
        return ResponseEntity.ok().body(habitDtoList);
    }

    @Operation(summary = "Get habit detail", description = "Return habit be id with list of days that user want to do it.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Return habit with list of days"),
            @ApiResponse(responseCode = "400", description = "Habit with this id doesn't exist or it's not user habit")
    })
    @GetMapping("/get/{id}")
    public ResponseEntity<HabitWithDaysDto> getUserHabitById(@AuthenticationPrincipal Jwt jwt, @PathVariable("id") Long id) {
        String userId = jwt.getClaimAsString("sub");
        logger.info("Get habit id:{} for user {}", id, userId);
        HabitWithDaysDto habit = this.habitService.getUserHabitById(userId, id);
        return ResponseEntity.ok(habit);
    }
    @Operation(summary = "Return habit to do be user in given days", description = "Its return habit in active state and with preferred execution day equal day of date or set to EVERYDAY")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful return of habit to do. If none found it will return empty list")
    })
    @GetMapping("/get/on")
    public ResponseEntity<List<HabitDto>> getHabitToDoInDay(@AuthenticationPrincipal Jwt jwt, @PathParam("date")LocalDate date){
        String userId = jwt.getClaimAsString("sub");
        logger.info("Get habits to do in day {} for user {}", date, userId);
        List<HabitDto> habits = this.habitService.getUserHabitToExecuteInDay(userId, date);
        return ResponseEntity.ok(habits);
    }

    @Operation(summary = "Create new habit")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Create new habit successful"),
            @ApiResponse(responseCode = "400", description = "Failed to create new habit. Habit with this name already exist for this user"),
            @ApiResponse(responseCode = "500", description = "Failed request doesn't contain all necessary elements")
    })
    @PostMapping("/add")
    public ResponseEntity<Void> saveHabit(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody HabitCreateRequestDto habitDto) {
        String userId = jwt.getClaimAsString("sub");
        logger.info("Add habit for user {}", userId);
        this.habitService.saveUserHabit(userId, habitMapper.toHabitDto(habitDto));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Add days of week to habit", description = "Add days of week that user want to do habit to habit selected habit")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Add days of week to habit successful"),
            @ApiResponse(responseCode = "400", description = "Habit with this id doesn't exist or it's not user habit"),
            @ApiResponse(responseCode = "500", description = "Failed request doesn't contain all necessary elements")
    })
    @PostMapping("/execution/day")
    public ResponseEntity<Void> addExecutionDays(@AuthenticationPrincipal Jwt jwt, @RequestBody ExecutionDayRequestDto executionDayRequest) {
        String userid = jwt.getClaimAsString("sub");
        logger.info("Add execution days user {}", userid);
        this.habitService.setHabitExecutionDays(userid, executionDayRequest.habitId(), executionDayRequest.executionDayList());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Activation of habit", description = "If user has enough points habit will be activate. It's mean user can now execute it. After habit activation user can change only execution days of habit update of habit itself will be unavailable")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful activation of habit. User now can execute habit. User points was reduced by unlock cost of habit"),
            @ApiResponse(responseCode = "400", description = "Unsuccessful activation. Habit doesn't exist or user doesn't have enough point to activate")
    })
    @PostMapping("/activate/{id}")
    public ResponseEntity<Void> activateHabit(@AuthenticationPrincipal Jwt jwt, @PathVariable("id") Long id) {
        String userId = jwt.getClaimAsString("sub");
        logger.info("Start purchase operation by user {}", userId);
        this.habitService.purchaseHabit(userId, id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Edit inactive habit")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful update of habit"),
            @ApiResponse(responseCode = "400", description = "Unsuccessful update of habit. Possible cause habit doesn't exist, user doesn't own it, habit is already active or completed")
    })
    @PostMapping("/update")
    public ResponseEntity<Void> updateHabit(@AuthenticationPrincipal Jwt jwt, @RequestBody HabitUpdateRequestDto habitUpdateRequestDto) {
        String userId = jwt.getClaim("sub");
        logger.info("Updating habit {} by user {}", habitUpdateRequestDto.id(), userId);
        this.habitService.updateHabit(userId, habitUpdateRequestDto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete habit from database", description = "Delete habit and all related execution history and execution preference")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Delete successful")
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteHabitById(@AuthenticationPrincipal Jwt jwt, @PathVariable("id") Long id) {
        String userId = jwt.getClaim("sub");
        logger.info("Deleting habit {} by user {}", id, userId);
        this.habitService.deleteHabit(userId, id);
        return ResponseEntity.noContent().build();
    }
}
