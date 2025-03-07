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

    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestBody Map<String, Object> eventData){
        String eventType = (String) eventData.get("type");
        Map<String, Object> userDetails = (Map<String, Object>) eventData.get("representation");
        if("REGISTER".equals(eventType)){
            String userId = (String) userDetails.get("id");
            pointsService.createUserPoints(userId);
        }
        return ResponseEntity.ok("OK");
    }
}
