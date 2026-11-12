package com.workmate.backend.dto.request;

import java.util.Map;

import jakarta.validation.constraints.NotNull;

public record UpdateMemoryPreferencesRequest(

        @NotNull(message = "Preferences cannot be null") 
        Map<String, Object> preferences

) {
}
