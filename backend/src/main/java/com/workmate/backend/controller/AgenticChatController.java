package com.workmate.backend.controller;

import com.workmate.backend.dto.request.AgenticChatRequest;
import com.workmate.backend.dto.response.AgenticChatResponse;
import com.workmate.backend.service.AgenticChatService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/claude")
public class AgenticChatController {

    private final AgenticChatService agenticChatService;

    public AgenticChatController(
            AgenticChatService agenticChatService) {
        this.agenticChatService = agenticChatService;
    }

    @PostMapping("/agentic-chat")
    public AgenticChatResponse chat(
            @Valid @RequestBody AgenticChatRequest request) {

        return agenticChatService.chat(request.message());
    }
}