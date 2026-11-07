package com.workmate.backend.dto.response;

public record LeaveBalanceResponse(
        String employeeId,
        int totalLeaves,
        int usedLeaves,
        int remainingLeaves,
        int pendingLeaves

) {
}