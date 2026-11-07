package com.workmate.backend.controller;

import com.workmate.backend.dto.request.ToolExecutionRequest;
import com.workmate.backend.dto.response.ToolExecutionResult;
import com.workmate.backend.service.ToolExecutionService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/claude/tools")
public class ToolExecutionController {

    private final ToolExecutionService toolExecutionService;
    private final String trustedEmployeeId;

    public ToolExecutionController(
            ToolExecutionService toolExecutionService,
            @Value("${workmate.demo-employee-id:EMP001}") String trustedEmployeeId) {

        this.toolExecutionService = toolExecutionService;
        this.trustedEmployeeId = trustedEmployeeId;
    }

    @PostMapping("/execute")
    public ToolExecutionResult executeTool(
            @RequestBody ToolExecutionRequest request) {

        return toolExecutionService.execute(
                request.toolName(),
                request.input(),
                trustedEmployeeId);
    }
}