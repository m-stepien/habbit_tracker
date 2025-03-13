package com.habit.tracker.core.controller;

import com.habit.tracker.core.dto.RegisterDataDto;
import com.habit.tracker.core.entity.PointsEntity;
import com.habit.tracker.core.service.PointsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/points")
public class PointsController {

    private static final Logger logger = LoggerFactory.getLogger(PointsController.class);
    private PointsService pointsService;

    @Autowired
    public PointsController(PointsService pointsService){
        this.pointsService = pointsService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestBody RegisterDataDto registerData){
        String eventType =  registerData.type();
        String userId = registerData.userId();
        logger.info("Creating points for user {} with event type {}", userId, eventType);
        if(this.pointsService.isUserExist(userId) && "REGISTER".equals(eventType)){
            pointsService.createUserPoints(userId);
        }
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/get")
    public ResponseEntity<PointsEntity> getUserPoints(@AuthenticationPrincipal Jwt jwt){
        String userId = jwt.getClaim("sub");
        logger.info("Get points for user {}", userId);
        PointsEntity userPoint = this.pointsService.getUserPoints(userId);
        return ResponseEntity.ok(userPoint);
    }
}
