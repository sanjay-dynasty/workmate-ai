package com.workmate.backend.dto.response;

public record AgenticChatResponse(

        Long conversationId,
        String answer,
        String model,
        String stopReason,
        int toolCalls,
        int iterations

) {
}