package com.habit.tracker.core.controller;

import com.habit.tracker.core.dto.RegisterDataDto;
import com.habit.tracker.core.entity.PointsEntity;
import com.habit.tracker.core.service.PointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<String> create(@RequestBody RegisterDataDto registerData){
        String eventType =  registerData.type();
        String userId = registerData.userId();
        if(this.pointsService.isUserExist(userId) && "REGISTER".equals(eventType)){
            pointsService.createUserPoints(userId);
        }
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/get")
    public ResponseEntity<PointsEntity> getUserPoints(@AuthenticationPrincipal Jwt jwt){
        PointsEntity userPoint = this.pointsService.getUserPoints(jwt.getClaimAsString("sub"));
        return ResponseEntity.ok(userPoint);
    }
}
