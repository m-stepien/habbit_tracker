package com.habit.tracker.core.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/public")
    public Map<String, String> publicEndpoint() {
        return Map.of("message", "To jest publiczny endpoint, każdy ma dostęp.");
    }

    @GetMapping("/user")
    public Map<String, Object> userEndpoint(@AuthenticationPrincipal Jwt jwt) {
        return Map.of(
                "message", "Witaj, użytkowniku!",
                "username", jwt.getClaim("preferred_username"),
                "roles", jwt.getClaimAsMap("realm_access")
        );
    }

    @GetMapping("/admin")
    public Map<String, Object> adminEndpoint(@AuthenticationPrincipal Jwt jwt) {
        return Map.of(
                "message", "Witaj, administratorze!",
                "username", jwt.getClaim("preferred_username"),
                "roles", jwt.getClaimAsMap("realm_access")
        );
    }
}
