package com.workmate.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ContextWindowService {

    private final ContextTokenEstimator tokenEstimator;

    private final int maxHistoryTokens;
    private final int maxHistoryMessages;
    private final int reservedTokens;

    public ContextWindowService(
            ContextTokenEstimator tokenEstimator,
            @Value("${workmate.context.max-history-tokens:12000}") int maxHistoryTokens,
            @Value("${workmate.context.max-history-messages:30}") int maxHistoryMessages,
            @Value("${workmate.context.reserved-tokens:3000}") int reservedTokens) {

        this.tokenEstimator = tokenEstimator;
        this.maxHistoryTokens = maxHistoryTokens;
        this.maxHistoryMessages = maxHistoryMessages;
        this.reservedTokens = reservedTokens;
    }

    // --------------------------------------------------
    // Select recent conversation context
    // --------------------------------------------------

    public List<Map<String, Object>> selectContext(
            String systemPrompt,
            List<Map<String, Object>> completeHistory) {

        if (completeHistory == null || completeHistory.isEmpty()) {
            return new ArrayList<>();
        }

        int systemTokens = tokenEstimator.estimateTokens(systemPrompt);

        int availableHistoryTokens = maxHistoryTokens - reservedTokens - systemTokens;

        if (availableHistoryTokens <= 0) {
            throw new IllegalStateException(
                    "System prompt exceeds the configured context budget.");
        }

        // Apply the maximum message count first.
        int startIndex = Math.max(
                0,
                completeHistory.size() - maxHistoryMessages);

        // Move forward until a normal user message is found.
        // Never begin with an isolated assistant response
        // or a tool_result message.
        while (startIndex < completeHistory.size()
                && !isNormalUserMessage(
                        completeHistory.get(startIndex))) {

            startIndex++;
        }

        if (startIndex >= completeHistory.size()) {
            throw new IllegalStateException(
                    "No complete user turn is available for context selection.");
        }

        List<Map<String, Object>> selected = new ArrayList<>(
                completeHistory.subList(
                        startIndex,
                        completeHistory.size()));

        // Ensure the selected history ends with a complete
        // tool exchange and stays within the estimated budget.
        while (true) {

            if (!endsWithCompleteToolExchange(selected)) {
                throw new IllegalStateException(
                        "Conversation history contains an incomplete tool exchange.");
            }

            int estimatedTokens = estimateHistoryTokens(selected);

            if (estimatedTokens <= availableHistoryTokens) {
                return selected;
            }

            // Remove the oldest complete user turn.
            int nextStart = findNextNormalUserMessage(selected);

            if (nextStart == -1) {
                throw new IllegalStateException(
                        "The latest conversation turn exceeds the configured "
                                + "context token budget.");
            }

            selected = new ArrayList<>(
                    selected.subList(
                            nextStart,
                            selected.size()));
        }
    }

    // --------------------------------------------------
    // Estimate selected history tokens
    // --------------------------------------------------

    private int estimateHistoryTokens(
            List<Map<String, Object>> messages) {

        int total = 0;

        for (Map<String, Object> message : messages) {

            total += tokenEstimator.estimateMessageTokens(
                    message.get("content"));
        }

        return total;
    }

    // --------------------------------------------------
    // Find the next normal user message
    // --------------------------------------------------

    private int findNextNormalUserMessage(
            List<Map<String, Object>> messages) {

        for (int i = 1; i < messages.size(); i++) {

            if (isNormalUserMessage(messages.get(i))) {
                return i;
            }
        }

        return -1;
    }

    // --------------------------------------------------
    // Identify a normal user message
    // --------------------------------------------------

    private boolean isNormalUserMessage(
            Map<String, Object> message) {

        if (!"user".equals(message.get("role"))) {
            return false;
        }

        // Normal user messages contain String content.
        // Tool-result messages contain structured List content.
        return message.get("content") instanceof String;
    }

    // --------------------------------------------------
    // Validate tool exchange completion
    // --------------------------------------------------

    private boolean endsWithCompleteToolExchange(
            List<Map<String, Object>> messages) {

        if (messages.isEmpty()) {
            return true;
        }

        Map<String, Object> lastMessage = messages.get(messages.size() - 1);

        // A selected history must not end with an assistant
        // message requesting tool execution.
        return !containsToolUse(lastMessage);
    }

    private boolean containsToolUse(
            Map<String, Object> message) {

        Object content = message.get("content");

        if (!(content instanceof List<?> blocks)) {
            return false;
        }

        for (Object item : blocks) {

            if (item instanceof Map<?, ?> block
                    && "tool_use".equals(block.get("type"))) {

                return true;
            }
        }

        return false;
    }
}
