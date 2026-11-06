package com.workmate.backend.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.workmate.backend.dto.request.ClaudeChatRequest;
import com.workmate.backend.dto.response.ClaudeChatResponse;
import com.workmate.backend.service.ClaudeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/claude")
public class ClaudeController {

    private final ClaudeService claudeService;

    public ClaudeController(ClaudeService claudeService) {
        this.claudeService = claudeService;
    }

    @PostMapping("/chat")
    public ClaudeChatResponse chat(@Valid @RequestBody ClaudeChatRequest request) {
        return claudeService.chat(request.message());
    }
}
