
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

    // Temporary development identity.
    private static final String DEMO_EMPLOYEE_ID = "EMP001";

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // --------------------------------------------------
    // 1. Get leave balance
    // --------------------------------------------------

    @GetMapping("/leaves/balance")
    public LeaveBalanceResponse getLeaveBalance() {

        return employeeService.getLeaveBalance(
                DEMO_EMPLOYEE_ID);
    }

    // --------------------------------------------------
    // 2. Get employee profile
    // --------------------------------------------------

    @GetMapping("/employees/profile")
    public EmployeeProfileResponse getEmployeeProfile() {

        return employeeService.getEmployeeProfile(
                DEMO_EMPLOYEE_ID);
    }

    // --------------------------------------------------
    // 3. Search company documents
    // --------------------------------------------------

    @GetMapping("/documents/search")
    public List<DocumentResponse> searchDocuments(
            @RequestParam(required = false) String query) {

        return employeeService.searchDocuments(query);
    }
}
