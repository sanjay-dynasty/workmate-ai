package com.workmate.backend.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record EmployeeMemoryResponse(

        Long id,
        String employeeId,
        Map<String, Object> preferences,
        List<String> activeGoals,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}
