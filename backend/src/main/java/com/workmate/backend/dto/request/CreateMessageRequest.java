package com.workmate.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateMessageRequest(

        @NotNull 
        @NotBlank 
        String role,

        @NotNull 
        @NotBlank 
        String content

) {
}