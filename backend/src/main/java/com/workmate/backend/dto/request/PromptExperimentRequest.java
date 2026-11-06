package com.workmate.backend.dto.request;

import com.workmate.backend.dto.PromptStrategy;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PromptExperimentRequest(

        @NotBlank
        String message,

        @NotNull 
        PromptStrategy strategy

) {
}
