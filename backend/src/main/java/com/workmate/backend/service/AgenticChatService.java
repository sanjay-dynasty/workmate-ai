
package com.workmate.backend.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.workmate.backend.dto.response.AgenticChatResponse;
import com.workmate.backend.dto.response.ToolExecutionResult;

@Service
public class AgenticChatService {

    private static final int MAX_TOOL_ROUNDS = 5;

    private final ClaudeService claudeService;
    private final WorkMateToolDefinitions toolDefinitions;
    private final ToolExecutionService toolExecutionService;
    private final WorkMateSystemPrompt workMateSystemPrompt;
    private final ObjectMapper objectMapper;
    private final String model;
    private final String trustedEmployeeId;

    public AgenticChatService(
            ClaudeService claudeService,
            WorkMateToolDefinitions toolDefinitions,
            ToolExecutionService toolExecutionService,
            WorkMateSystemPrompt workMateSystemPrompt,
            ObjectMapper objectMapper,
            @Value("${anthropic.model}") String model,
            @Value("${workmate.demo-employee-id:EMP001}") String trustedEmployeeId) {

        this.claudeService = claudeService;
        this.toolDefinitions = toolDefinitions;
        this.toolExecutionService = toolExecutionService;
        this.workMateSystemPrompt = workMateSystemPrompt;
        this.objectMapper = objectMapper;
        this.model = model;
        this.trustedEmployeeId = trustedEmployeeId;
    }

    public AgenticChatResponse chat(String userMessage) {

        if (userMessage == null || userMessage.isBlank()) {
            throw new IllegalArgumentException(
                    "User message cannot be empty.");
        }

        if (trustedEmployeeId == null
                || trustedEmployeeId.isBlank()) {
            throw new IllegalStateException(
                    "Trusted employee identity is not configured.");
        }

        // Conversation history for this request.
        List<Map<String, Object>> messages = new ArrayList<>();

        messages.add(Map.of(
                "role", "user",
                "content", userMessage));

        // Tool definitions available to Claude.
        List<Map<String, Object>> tools = toolDefinitions.getTools();

        int totalToolCalls = 0;

        // Initial request plus a maximum of five tool-use rounds.
        for (int requestNumber = 1; requestNumber <= MAX_TOOL_ROUNDS + 1; requestNumber++) {

            Map<String, Object> requestBody = new LinkedHashMap<>();

            requestBody.put("model", model);
            requestBody.put("max_tokens", 1500);

            requestBody.put(
                    "system",
                    workMateSystemPrompt.getChatSystemPrompt()
                            + "\n\n<employee_context>"
                            + "\nCurrent demo employee ID: "
                            + trustedEmployeeId
                            + "\nUse this employee ID whenever a tool requires employeeId."
                            + "\nDo not ask the user for their employee ID."
                            + "\n</employee_context>");

            requestBody.put("tools", tools);
            requestBody.put("messages", messages);

            // Send request to Claude.
            JsonNode response = claudeService.sendMessagesRequest(requestBody);

            if (response == null || response.isNull()) {
                throw new IllegalStateException(
                        "Empty response received from Claude.");
            }

            String stopReason = response.path("stop_reason").asText();

            // Claude has completed its answer.
            if ("end_turn".equals(stopReason)) {

                return new AgenticChatResponse(
                        extractText(response),
                        response.path("model").asText(),
                        stopReason,
                        totalToolCalls,
                        requestNumber);
            }

            // Claude wants the backend to execute tools.
            if ("tool_use".equals(stopReason)) {

                if (requestNumber > MAX_TOOL_ROUNDS) {
                    throw new IllegalStateException(
                            "Maximum tool execution rounds exceeded.");
                }

                List<Map<String, Object>> assistantContent = objectMapper.convertValue(
                        response.path("content"),
                        new TypeReference<>() {
                        });

                // Preserve Claude's complete assistant response,
                // including text and tool_use blocks.
                messages.add(Map.of(
                        "role", "assistant",
                        "content", assistantContent));

                List<Map<String, Object>> toolResults = new ArrayList<>();

                // Process every tool_use block in Claude's response.
                for (JsonNode block : response.path("content")) {

                    if (!"tool_use".equals(
                            block.path("type").asText())) {
                        continue;
                    }

                    String toolName = block.path("name").asText();

                    String toolUseId = block.path("id").asText();

                    if (toolName.isBlank() || toolUseId.isBlank()) {
                        throw new IllegalStateException(
                                "Claude returned an invalid tool request.");
                    }

                    Map<String, Object> input = objectMapper.convertValue(
                            block.path("input"),
                            new TypeReference<>() {
                            });

                    // Execute the Java backend operation using
                    // the server-controlled employee identity.
                    ToolExecutionResult result = toolExecutionService.execute(
                            toolName,
                            input,
                            trustedEmployeeId);

                    totalToolCalls++;

                    // Convert the execution result into JSON.
                    String resultJson;

                    try {
                        resultJson = objectMapper.writeValueAsString(result);

                    } catch (Exception exception) {
                        throw new IllegalStateException(
                                "Unable to serialize tool result.",
                                exception);
                    }

                    // Prepare Anthropic tool_result block.
                    Map<String, Object> resultBlock = new LinkedHashMap<>();

                    resultBlock.put("type", "tool_result");
                    resultBlock.put("tool_use_id", toolUseId);
                    resultBlock.put("content", resultJson);

                    if (!result.success()) {
                        resultBlock.put("is_error", true);
                    }

                    toolResults.add(resultBlock);
                }

                if (toolResults.isEmpty()) {
                    throw new IllegalStateException(
                            "Claude requested tool use but no valid "
                                    + "tool_use blocks were found.");
                }

                // Send all tool results back to Claude.
                messages.add(Map.of(
                        "role", "user",
                        "content", toolResults));

                // Continue the agentic loop.
                continue;
            }

            // Claude refused the request.
            if ("refusal".equals(stopReason)) {

                return new AgenticChatResponse(
                        extractText(response),
                        response.path("model").asText(),
                        stopReason,
                        totalToolCalls,
                        requestNumber);
            }

            throw new IllegalStateException(
                    "Claude stopped unexpectedly. Stop reason: "
                            + stopReason);
        }

        throw new IllegalStateException(
                "Agentic execution ended without a final answer.");
    }

    // --------------------------------------------------
    // Extract text blocks from Claude's response
    // --------------------------------------------------

    private String extractText(JsonNode response) {

        List<String> textBlocks = new ArrayList<>();

        for (JsonNode block : response.path("content")) {

            if ("text".equals(block.path("type").asText())) {

                String text = block.path("text").asText("");

                if (!text.isBlank()) {
                    textBlocks.add(text);
                }
            }
        }

        return String.join("\n", textBlocks);
    }
}
