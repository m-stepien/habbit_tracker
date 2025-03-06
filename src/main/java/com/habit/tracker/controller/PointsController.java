package com.habit.tracker.controller;

import com.habit.tracker.service.PointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/points")
public class PointsController {

    private PointsService pointsService;

    @Autowired
    public PointsController(PointsService pointsService){
        this.pointsService = pointsService;
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody Map<String, Object> eventData){
        this.pointsService.createUserPoints(eventData.get())
    }
}
