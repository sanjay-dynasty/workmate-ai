package com.workmate.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record AgenticChatRequest(

        @Positive(message = "Conversation ID must be positive.")
        Long conversationId,

        @NotBlank(message = "Message cannot be empty.")
        String message

) {
}