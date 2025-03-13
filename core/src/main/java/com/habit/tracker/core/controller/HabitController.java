package com.habit.tracker.core.controller;

import com.habit.tracker.core.dto.*;
import com.habit.tracker.core.mapper.HabitMapper;
import com.habit.tracker.core.service.HabitService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/habit")
public class HabitController {
    private final HabitService habitService;
    private final HabitMapper habitMapper;
    private static final Logger logger = LoggerFactory.getLogger(HabitController.class);

    @Autowired
    HabitController(HabitService habitService, HabitMapper habitMapper){
        this.habitService = habitService;
        this.habitMapper = habitMapper;
    }

    @GetMapping("/get/all")
    public ResponseEntity<List<HabitDto>> getUserHabitsList(@AuthenticationPrincipal Jwt jwt){
        String userId = jwt.getClaimAsString("sub");
        logger.info("Start get all habit of user {}", userId);
        List<HabitDto> habitDtoList = this.habitService.getUserHabits(userId);
        return ResponseEntity.ok().body(habitDtoList);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<HabitWithDaysDto> getUserHabitById(@AuthenticationPrincipal Jwt jwt, @PathVariable("id") Long id){
        String userId = jwt.getClaimAsString("sub");
        logger.info("Get habit id:{} for user {}", id, userId);
        HabitWithDaysDto habit = this.habitService.getUserHabitById(userId, id);
        return ResponseEntity.ok(habit);
    }

    @PostMapping("/add")
    public ResponseEntity<Void> saveHabit(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody HabitCreateRequestDto habitDto){
        String userId = jwt.getClaimAsString("sub");
        logger.info("Add habit for user {}", userId);
        this.habitService.saveUserHabit(userId, habitMapper.toHabitDto(habitDto));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/execution/day")
    public ResponseEntity<Void> addExecutionDays(@AuthenticationPrincipal Jwt jwt, @RequestBody ExecutionDayRequestDto executionDayRequest){
        String userid = jwt.getClaimAsString("sub");
        logger.info("Add execution days user {}", userid);
        this.habitService.setHabitExecutionDays(userid, executionDayRequest.habitId(), executionDayRequest.executionDayList());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/activate/{id}")
    public ResponseEntity<Void> activateHabit(@AuthenticationPrincipal Jwt jwt, @PathVariable("id") Long id){
        String userId = jwt.getClaimAsString("sub");
        logger.info("Start purchase operation by user {}", userId);
        this.habitService.purchaseHabit(userId, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/update")
    public ResponseEntity<Void> updateHabit(@AuthenticationPrincipal Jwt jwt, @RequestBody HabitUpdateRequestDto habitUpdateRequestDto){
        String userId = jwt.getClaim("sub");
        logger.info("Updating habit {} by user {}", habitUpdateRequestDto.id(), userId);
        this.habitService.updateHabit(userId, habitUpdateRequestDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteHabitById(@AuthenticationPrincipal Jwt jwt, @PathVariable("id") Long id){
        String userId = jwt.getClaim("sub");
        logger.info("Deleting habit {} by user {}", id, userId);
        this.habitService.deleteHabit(userId, id);
        return ResponseEntity.noContent().build();
    }
}
