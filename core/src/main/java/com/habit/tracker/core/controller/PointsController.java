package com.habit.tracker.core.controller;

import com.habit.tracker.core.dto.RegisterDataDto;
import com.habit.tracker.core.entity.PointsEntity;
import com.habit.tracker.core.service.PointsService;
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

import java.util.Map;

@RestController
@RequestMapping("/points")
public class PointsController {

    private static final Logger logger = LoggerFactory.getLogger(PointsController.class);
    private PointsService pointsService;

    @Autowired
    public PointsController(PointsService pointsService) {
        this.pointsService = pointsService;
    }

    @Operation(summary = "Create user points after registration on keycloak", description = "Endpoint for custom keycloak listener provider. When new user register listener provider send REGISTER request. After validating request data service create points for user with default value from application.yml.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Points successfully created")}
    )
    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestBody RegisterDataDto registerData) {
        String eventType = registerData.type();
        String userId = registerData.userId();
        logger.info("Creating points for user {} with event type {}", userId, eventType);
        if (this.pointsService.isUserExist(userId) && "REGISTER".equals(eventType)) {
            pointsService.createUserPoints(userId);
        }
        return ResponseEntity.ok("OK");
    }

    @Operation(description = "Return points of user", summary = "If user exist return current value of point for userId from jwt token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful returning point of user"),
            @ApiResponse(responseCode = "400", description = "User was not found in database")
    })
    @GetMapping("/get")
    public ResponseEntity<PointsEntity> getUserPoints(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getClaim("sub");
        logger.info("Get points for user {}", userId);
        PointsEntity userPoint = this.pointsService.getUserPoints(userId);
        return ResponseEntity.ok(userPoint);
    }
}
