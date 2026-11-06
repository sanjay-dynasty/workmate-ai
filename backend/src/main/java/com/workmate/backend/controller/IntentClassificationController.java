package com.workmate.backend.controller;

import com.workmate.backend.dto.response.IntentClassificationResponse;
import com.workmate.backend.service.ClaudeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/claude")
public class IntentClassificationController {

    private final ClaudeService claudeService;

    public IntentClassificationController(
            ClaudeService claudeService) {

        this.claudeService = claudeService;
    }

    public record ClassificationRequest(
            @NotBlank String message) {
    }

    @PostMapping("/classify")
    public IntentClassificationResponse classify(
            @Valid @RequestBody ClassificationRequest request) {

        return claudeService.classifyRequest(request.message());
    }
}