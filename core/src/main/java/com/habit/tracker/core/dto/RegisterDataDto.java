package com.habit.tracker.core.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Data transfer object for keycloak register event")
public record RegisterDataDto(
        @Schema(description = "Event type, expected 'REGISTER'", example = "REGISTER")
        @NotBlank
        String type,
        @Schema(description = "User ID assigned by Keycloak", example = "a1b2c3d4-5678-90ab-cdef-1234567890ab")
        @NotBlank
        String userId,
        @Schema(description = "Client ID from which the registration occurred", example = "habit-tracker-client")
        @NotBlank
        String clientid) {
}
