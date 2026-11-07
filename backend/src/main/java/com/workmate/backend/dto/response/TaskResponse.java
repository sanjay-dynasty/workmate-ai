package com.workmate.backend.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TaskResponse(

        Long id,
        String taskName,
        String description,
        String employeeId,
        String priority,
        String status,
        LocalDate dueDate,
        LocalDateTime createdAt

) {
}
