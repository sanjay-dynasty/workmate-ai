package com.workmate.backend.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;

public record CreateTaskRequest(

        @NotBlank(message = "Task name is requiredd") 
        String taskName,

        String description,

        @NotBlank(message = "Employee id is required") 
        String employeeId,

        String priority,

        LocalDate dueDate

) {
}
