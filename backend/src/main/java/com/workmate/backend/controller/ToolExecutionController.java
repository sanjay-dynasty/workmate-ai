package com.workmate.backend.controller;

import com.workmate.backend.dto.request.ToolExecutionRequest;
import com.workmate.backend.dto.response.ToolExecutionResult;
import com.workmate.backend.service.ToolExecutionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/claude/tools")
public class ToolExecutionController {

    private final ToolExecutionService toolExecutionService;

    public ToolExecutionController(
            ToolExecutionService toolExecutionService) {

        this.toolExecutionService = toolExecutionService;
    }

    @PostMapping("/execute")
    public ToolExecutionResult executeTool(
            @RequestBody ToolExecutionRequest request) {

        return toolExecutionService.execute(
                request.toolName(),
                request.input());
    }
}