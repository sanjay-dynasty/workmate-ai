package com.workmate.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.workmate.backend.dto.request.AddMemoryGoalRequest;
import com.workmate.backend.dto.request.UpdateEmployeeMemoryRequest;
import com.workmate.backend.dto.request.UpdateMemoryPreferencesRequest;
import com.workmate.backend.dto.response.EmployeeMemoryResponse;
import com.workmate.backend.service.EmployeeMemoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/memory")
public class EmployeeMemoryController {

    // Temporary development identity.
    // Later, obtain this from Spring Security authentication.
    private static final String DEMO_EMPLOYEE_ID = "EMP001";

    private final EmployeeMemoryService memoryService;

    public EmployeeMemoryController(
            EmployeeMemoryService memoryService) {

        this.memoryService = memoryService;
    }

    // Retrieve employee memory.
    @GetMapping
    public EmployeeMemoryResponse getMemory() {

        return memoryService.getMemory(DEMO_EMPLOYEE_ID);
    }

    // Create or replace employee memory.
    @PutMapping
    public EmployeeMemoryResponse updateMemory(
            @Valid @RequestBody UpdateEmployeeMemoryRequest request) {

        return memoryService.updateMemory(
                DEMO_EMPLOYEE_ID,
                request);
    }

    // Update selected preferences.
    @PatchMapping("/preferences")
    public EmployeeMemoryResponse updatePreferences(
            @Valid @RequestBody UpdateMemoryPreferencesRequest request) {

        return memoryService.updatePreferences(
                DEMO_EMPLOYEE_ID,
                request.preferences());
    }

    // Add an active goal.
    @PostMapping("/goals")
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeMemoryResponse addGoal(
            @Valid @RequestBody AddMemoryGoalRequest request) {

        return memoryService.addGoal(
                DEMO_EMPLOYEE_ID,
                request.goal());
    }

    // Remove an active goal.
    @DeleteMapping("/goals")
    public EmployeeMemoryResponse removeGoal(
            @RequestParam String goal) {

        return memoryService.removeGoal(
                DEMO_EMPLOYEE_ID,
                goal);
    }
}
