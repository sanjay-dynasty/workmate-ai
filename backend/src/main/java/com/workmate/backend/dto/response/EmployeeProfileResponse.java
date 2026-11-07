package com.workmate.backend.dto.response;

public record EmployeeProfileResponse(
        String employeeId,
        String name,
        String email,
        String department,
        String designation,
        String manager,
        String location

) {
}