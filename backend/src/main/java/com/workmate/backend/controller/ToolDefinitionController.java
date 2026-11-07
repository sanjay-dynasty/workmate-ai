package com.workmate.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.workmate.backend.service.WorkMateToolDefinitions;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/claude")
public class ToolDefinitionController {

    private final WorkMateToolDefinitions workMateToolDefinitions;

    public ToolDefinitionController(WorkMateToolDefinitions workMateToolDefinitions) {
        this.workMateToolDefinitions = workMateToolDefinitions;
    }

    @GetMapping("/tools")
    public List<Map<String, Object>> getTools() {
        return workMateToolDefinitions.getTools();
    }

}
