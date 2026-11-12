package com.workmate.backend.dto.request;

import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.NotNull;

public record UpdateEmployeeMemoryRequest(

        @NotNull(message = "Preferences cannot be null") 
        Map<String, Object> preferences,

        @NotNull(message = "Active goals cannot be null") 
        List<String> activeGoals

) {
}
