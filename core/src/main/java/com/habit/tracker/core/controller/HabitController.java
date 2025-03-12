package com.habit.tracker.core.controller;

import com.habit.tracker.core.dto.ExecutionDayRequestDto;
import com.habit.tracker.core.dto.HabitCreateRequestDto;
import com.habit.tracker.core.dto.HabitDto;
import com.habit.tracker.core.dto.HabitWithDaysDto;
import com.habit.tracker.core.mapper.HabitMapper;
import com.habit.tracker.core.service.HabitService;
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

    @Autowired
    HabitController(HabitService habitService, HabitMapper habitMapper){
        this.habitService = habitService;
        this.habitMapper = habitMapper;
    }

    @GetMapping("/get/all")
    public ResponseEntity<List<HabitDto>> getUserHabitsList(@AuthenticationPrincipal Jwt jwt){
        List<HabitDto> habitDtoList = this.habitService.getUserHabits(jwt.getClaimAsString("sub"));
        return ResponseEntity.ok().body(habitDtoList);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<HabitWithDaysDto> getUserHabitById(@AuthenticationPrincipal Jwt jwt, @PathVariable("id") Long id){
        HabitWithDaysDto habit = this.habitService.getUserHabitById(jwt.getClaimAsString("sub"), id);
        return ResponseEntity.ok(habit);
    }

    @PostMapping("/add")
    public ResponseEntity<Void> saveHabit(@AuthenticationPrincipal Jwt jwt, @RequestBody HabitCreateRequestDto habitDto){
        this.habitService.saveUserHabit(jwt.getClaimAsString("sub"), habitMapper.toHabitDto(habitDto));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/execution/day")
    public ResponseEntity<Void> addExecutionDays(@AuthenticationPrincipal Jwt jwt, @RequestBody ExecutionDayRequestDto executionDayRequest){
        this.habitService.setHabitExecutionDays(jwt.getClaimAsString("sub"), executionDayRequest.habitId(), executionDayRequest.executionDayList());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/activate/{id}")
    public ResponseEntity<Void> activateHabit(@AuthenticationPrincipal Jwt jwt, @PathVariable("id") Long id){
        this.habitService.purchaseHabit(jwt.getClaimAsString("sub"), id);
        return ResponseEntity.noContent().build();
    }
}
