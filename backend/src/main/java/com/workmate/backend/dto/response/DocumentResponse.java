package com.workmate.backend.dto.response;

public record DocumentResponse(
        String documentId,
        String title,
        String description,
        String category,
        String content

) {
}