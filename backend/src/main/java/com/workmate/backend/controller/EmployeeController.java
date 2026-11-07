package com.workmate.backend.controller;

import com.workmate.backend.dto.response.DocumentResponse;
import com.workmate.backend.dto.response.EmployeeProfileResponse;
import com.workmate.backend.dto.response.LeaveBalanceResponse;
import com.workmate.backend.service.EmployeeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/leaves/balance")
    public LeaveBalanceResponse getLeaveBalance(
            @RequestParam(defaultValue = "EMP001") String employeeId) {

        return employeeService.getLeaveBalance(employeeId);
    }

    @GetMapping("/employees/profile")
    public EmployeeProfileResponse getEmployeeProfile(
            @RequestParam(defaultValue = "EMP001") String employeeId) {

        return employeeService.getEmployeeProfile(employeeId);
    }

    @GetMapping("/documents/search")
    public List<DocumentResponse> searchDocuments(
            @RequestParam(required = false) String query) {

        return employeeService.searchDocuments(query);
    }
}