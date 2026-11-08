package com.workmate.backend.service;

import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.workmate.backend.dto.request.CreateMessageRequest;
import com.workmate.backend.dto.response.ConversationMessageResponse;
import com.workmate.backend.dto.response.ConversationResponse;
import com.workmate.backend.entity.Conversation;
import com.workmate.backend.entity.ConversationMessage;
import com.workmate.backend.repository.ConversationMessageRepository;
import com.workmate.backend.repository.ConversationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConversationService {

        private static final Set<String> ALLOWED_ROLES = Set.of("USER", "ASSISTANT");

        private final ConversationRepository conversationRepository;
        private final ConversationMessageRepository messageRepository;

        // Create a new conversation.
        @Transactional
        public ConversationResponse createConversation(String employeeId) {

                if (employeeId == null || employeeId.isBlank()) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Employee ID is required.");
                }

                Conversation conversation = new Conversation(employeeId.trim());

                Conversation saved = conversationRepository.save(conversation);

                return mapConversation(saved);
        }

        // Save a user or assistant message.
        @Transactional
        public ConversationMessageResponse addMessage(
                        Long conversationId,
                        CreateMessageRequest request) {

                Conversation conversation = conversationRepository.findById(conversationId)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Conversation not found."));

                String role = request.role().trim().toUpperCase();

                if (!ALLOWED_ROLES.contains(role)) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Role must be USER or ASSISTANT.");
                }

                ConversationMessage message = new ConversationMessage(
                                conversation,
                                role,
                                request.content().trim());

                ConversationMessage saved = messageRepository.save(message);

                return mapMessage(saved);
        }

        // Retrieve conversations for an employee.
        @Transactional(readOnly = true)
        public List<ConversationResponse> getConversations(String employeeId) {

                if (employeeId == null || employeeId.isBlank()) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Employee ID is required.");
                }

                return conversationRepository
                                .findByEmployeeIdOrderByCreatedAtDesc(employeeId.trim())
                                .stream()
                                .map(this::mapConversation)
                                .toList();
        }

        // Retrieve messages in chronological order.
        @Transactional(readOnly = true)
        public List<ConversationMessageResponse> getMessages(
                        Long conversationId) {

                if (!conversationRepository.existsById(conversationId)) {
                        throw new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Conversation not found.");
                }

                return messageRepository
                                .findByConversationIdOrderByIdAsc(conversationId)
                                .stream()
                                .map(this::mapMessage)
                                .toList();
        }

        private ConversationResponse mapConversation(
                        Conversation conversation) {

                return new ConversationResponse(
                                conversation.getId(),
                                conversation.getEmployeeId(),
                                conversation.getCreatedAt());
        }

        private ConversationMessageResponse mapMessage(
                        ConversationMessage message) {

                return new ConversationMessageResponse(
                                message.getId(),
                                message.getConversation().getId(),
                                message.getRole(),
                                message.getContent(),
                                message.getCreatedAt());
        }
}