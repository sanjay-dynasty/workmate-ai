package com.workmate.backend.service;

import org.springframework.stereotype.Service;

@Service
public class ContextTokenEstimator {

    /*
     * Approximation:
     * 1 token is estimated as 4 characters.
     *
     * This is a budgeting heuristic, not an exact tokenizer.
     */
    private static final int CHARACTERS_PER_TOKEN = 4;

    /*
     * Additional allowance for message structure,
     * JSON formatting and tool metadata.
     */
    private static final int MESSAGE_OVERHEAD = 10;

    public int estimateTokens(String text) {

        if (text == null || text.isBlank()) {
            return 0;
        }

        return (int) Math.ceil(
                (double) text.length() / CHARACTERS_PER_TOKEN);
    }

    public int estimateMessageTokens(Object content) {

        if (content == null) {
            return MESSAGE_OVERHEAD;
        }

        String text = content.toString();

        return estimateTokens(text) + MESSAGE_OVERHEAD;
    }

    public int estimateTotalTokens(
            String systemPrompt,
            java.util.List<java.util.Map<String, Object>> messages) {

        int totalTokens = estimateTokens(systemPrompt);

        for (java.util.Map<String, Object> message : messages) {

            totalTokens += estimateMessageTokens(
                    message.get("content"));
        }

        return totalTokens;
    }
}