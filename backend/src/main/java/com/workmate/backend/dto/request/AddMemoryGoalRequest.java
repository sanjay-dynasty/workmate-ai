package com.workmate.backend.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AddMemoryGoalRequest(

        @NotBlank(message = "Goal cannot be empty")
        String goal

) {
}
