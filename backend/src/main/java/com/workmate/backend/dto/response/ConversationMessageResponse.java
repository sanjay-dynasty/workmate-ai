package com.workmate.backend.dto.response;

import java.time.LocalDateTime;

public record ConversationMessageResponse(
        Long id,
        Long conversationId,
        String role,
        String content,
        LocalDateTime createdAt

) {
}