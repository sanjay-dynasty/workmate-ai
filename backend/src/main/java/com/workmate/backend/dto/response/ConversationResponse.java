package com.workmate.backend.dto.response;

import java.time.LocalDateTime;

public record ConversationResponse(
        Long id,
        String employeeId,
        LocalDateTime createdAt

) {
}