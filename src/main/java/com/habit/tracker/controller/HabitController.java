package com.habit.tracker.controller;

import com.habit.tracker.dto.HabitDto;
import com.habit.tracker.service.HabitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/habit")
public class HabitController {
    private HabitService habitService;

    @Autowired
    HabitController(HabitService habitService){
        this.habitService = habitService;
    }

    @GetMapping("/get/all")
    public ResponseEntity<List<HabitDto>> getHabitsList(@AuthenticationPrincipal Jwt jwt){
        List<HabitDto> habitDtoList = this.habitService.getUserHabits(jwt.getClaimAsString("sub"));
        return ResponseEntity.ok().body(habitDtoList);
    }

    @PostMapping("/add")
    public ResponseEntity<Void> saveHabit(@AuthenticationPrincipal Jwt jwt, @RequestBody HabitDto habitDto){
        this.habitService.saveUserHabit(jwt.getClaimAsString("sub"), habitDto);
        return ResponseEntity.noContent().build();
    }

}
