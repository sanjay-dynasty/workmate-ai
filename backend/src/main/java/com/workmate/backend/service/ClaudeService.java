package com.workmate.backend.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import com.workmate.backend.dto.response.ClaudeChatResponse;

import tools.jackson.databind.JsonNode;

@Service
public class ClaudeService {

    private final RestClient claudRestClient;

    @Value("${anthropic.model}")
    private final String model;

    public ClaudeService(
            RestClient claudRestClient,
            @Value("${anthropic.model}") String model) {
        this.claudRestClient = claudRestClient;
        this.model = model;
    }

    public ClaudeChatResponse chat(String userMessage) {

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "max_tokens", 500,
                "system", """
                        You are WorkMate, an AI employee assistant.

                        Your responsibilities:
                        1. Help employees manage their work.
                        2. Assist with task planning.
                        3. Explain technical concepts.
                        4. Provide clear and helpful responses.

                        Be professional, concise and accurate.
                        Do not invent employee records or claim
                        to have performed actions you have not performed.
                        """,
                "messages", List.of(
                        Map.of(
                                "role", "user",
                                "content", userMessage))

        );

        JsonNode response = claudRestClient.post()
                .uri("/v1/messages")
                .body(requestBody)
                .retrieve()
                .body(JsonNode.class);

        if (response == null) {
            throw new IllegalStateException("Empty response received from Claude");
        }

        String answer = response.path("content")
                .findValue("text")
                .asString();

        String responseModel = response.path("model").asString();
        String stopReason = response.path("stop_reason").asString();

        return new ClaudeChatResponse(
                answer,
                responseModel,
                stopReason

        );

    }
}
