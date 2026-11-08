package com.workmate.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.workmate.backend.dto.request.CreateMessageRequest;
import com.workmate.backend.dto.response.ConversationMessageResponse;
import com.workmate.backend.dto.response.ConversationResponse;
import com.workmate.backend.service.ConversationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/conversations")
public class ConversationController {

    private final ConversationService conversationService;

    // Create a conversation.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ConversationResponse createConversation(
            @RequestParam(defaultValue = "EMP001") String employeeId) {

        return conversationService.createConversation(employeeId);
    }

    // Get all conversations for an employee.
    @GetMapping
    public List<ConversationResponse> getConversations(
            @RequestParam(defaultValue = "EMP001") String employeeId) {

        return conversationService.getConversations(employeeId);
    }

    // Save a message in a conversation.
    @PostMapping("/{conversationId}/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public ConversationMessageResponse addMessage(
            @PathVariable Long conversationId,
            @Valid @RequestBody CreateMessageRequest request) {

        return conversationService.addMessage(conversationId, request);
    }

    // Get all messages in a conversation.
    @GetMapping("/{conversationId}/messages")
    public List<ConversationMessageResponse> getMessages(
            @PathVariable Long conversationId) {

        return conversationService.getMessages(conversationId);
    }
}