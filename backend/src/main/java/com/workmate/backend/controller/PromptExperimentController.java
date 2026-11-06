package com.workmate.backend.controller;

import com.workmate.backend.dto.request.PromptExperimentRequest;
import com.workmate.backend.service.ClaudeService;
import com.workmate.backend.service.PromptTemplateService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/claude/experiments")
public class PromptExperimentController {

    private final ClaudeService claudeService;
    private final PromptTemplateService promptTemplateService;

    public PromptExperimentController(
            ClaudeService claudeService,
            PromptTemplateService promptTemplateService) {

        this.claudeService = claudeService;
        this.promptTemplateService = promptTemplateService;
    }

    @PostMapping("/classify")
    public String classify(
            @Valid @RequestBody PromptExperimentRequest request) {

        String prompt = promptTemplateService.getPrompt(
                request.strategy()
        );

        return claudeService.chatWithSystemPrompt(
                prompt,
                request.message()
        );
    }
}