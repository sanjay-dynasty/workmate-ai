package com.workmate.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.workmate.backend.dto.response.ClaudeChatResponse;
import com.workmate.backend.dto.response.IntentClassificationResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import java.time.ZoneId;

@Service
public class ClaudeService {

        private final RestClient claudeRestClient;
        private final String model;

        private final WorkMateSystemPrompt workMateSystemPrompt;
        private final ObjectMapper objectMapper;
        private final SchemaValidationService schemaValidationService;
        private final IntentResponseValidator intentResponseValidator;

        public ClaudeService(
                        RestClient claudeRestClient,
                        @Value("${anthropic.model}") String model,
                        WorkMateSystemPrompt workMateSystemPrompt,
                        ObjectMapper objectMapper,
                        SchemaValidationService schemaValidationService,
                        IntentResponseValidator intentResponseValidator) {

                this.claudeRestClient = claudeRestClient;
                this.model = model;
                this.workMateSystemPrompt = workMateSystemPrompt;
                this.objectMapper = objectMapper;
                this.schemaValidationService = schemaValidationService;
                this.intentResponseValidator = intentResponseValidator;
        }

        // PHASE 0: Basic Claude conversation

        public ClaudeChatResponse chat(String userMessage) {

                Map<String, Object> requestBody = Map.of(
                                "model", model,
                                "max_tokens", 500,
                                "system", workMateSystemPrompt.getChatSystemPrompt(),
                                "messages", List.of(
                                                Map.of(
                                                                "role", "user",
                                                                "content", userMessage)));

                JsonNode response = postForJson(requestBody);

                if (response == null) {
                        throw new IllegalStateException(
                                        "Empty response received from Claude");
                }

                String answer = extractText(response);

                return new ClaudeChatResponse(
                                answer,
                                response.path("model").asText(),
                                response.path("stop_reason").asText());
        }

        // PHASE 1: Prompt experiment

        public String chatWithSystemPrompt(
                        String systemPrompt,
                        String userMessage) {

                Map<String, Object> requestBody = Map.of(
                                "model", model,
                                "max_tokens", 300,
                                "system", systemPrompt,
                                "messages", List.of(
                                                Map.of(
                                                                "role", "user",
                                                                "content", userMessage)));

                JsonNode response = postForJson(requestBody);

                if (response == null) {
                        throw new IllegalStateException(
                                        "Empty Claude response");
                }

                return extractText(response);
        }

        // PHASE 1: Generate schema-constrained JSON

        private String generateStructuredResponse(String userMessage) {

                try {
                        JsonNode schemaNode = objectMapper.readTree(
                                        new ClassPathResource(
                                                        "schemas/intent-classification-schema.json").getInputStream());

                        // Convert Jackson JsonNode to ordinary Java Maps/Lists.
                        // This avoids serializing Jackson 2 JsonNode internals when
                        // Spring Boot's HTTP message converter uses a different Jackson version.
                        Map<String, Object> schema = objectMapper.convertValue(
                                        schemaNode,
                                        new TypeReference<Map<String, Object>>() {
                                        });

                        String systemPrompt = workMateSystemPrompt.getSystemPrompt()
                                        + "\\n\\n<runtime_context>\\n"
                                        + "Current date: " + LocalDate.now(ZoneId.of("Asia/Kolkata")) + "\\n"
                                        + "Timezone: Asia/Kolkata.\\n"
                                        + "For relative due dates such as tomorrow, return an ISO-8601 date (yyyy-MM-dd).\\n"
                                        + "If a field is unknown or not applicable, return null.\\n"
                                        + "</runtime_context>";

                        Map<String, Object> requestBody = Map.of(
                                        "model", model,
                                        "max_tokens", 700,
                                        "system", systemPrompt,
                                        "messages", List.of(
                                                        Map.of(
                                                                        "role", "user",
                                                                        "content", userMessage)),
                                        "output_config", Map.of(
                                                        "format", Map.of(
                                                                        "type", "json_schema",
                                                                        "schema", schema)));

                        JsonNode response = postForJson(requestBody);

                        if (response == null) {
                                throw new IllegalStateException("Empty response from Claude");
                        }

                        String stopReason = response.path("stop_reason").asText();
                        if (!"end_turn".equals(stopReason)) {
                                throw new IllegalStateException(
                                                "Claude response incomplete. Stop reason: " + stopReason);
                        }

                        for (JsonNode block : response.path("content")) {
                                if ("text".equals(block.path("type").asText())) {
                                        String text = block.path("text").asText(null);
                                        if (text != null && !text.isBlank()) {
                                                return text;
                                        }
                                }
                        }

                        throw new IllegalStateException("No text content returned by Claude");

                } catch (IOException exception) {
                        throw new IllegalStateException("Unable to load intent JSON schema", exception);
                }
        }

        // PHASE 1: Validate and deserialize Claude response

        public IntentClassificationResponse classifyRequest(
                        String userMessage) {

                String json = generateStructuredResponse(userMessage);

                schemaValidationService.validate(json);

                try {
                        IntentClassificationResponse result = objectMapper.readValue(
                                        json,
                                        IntentClassificationResponse.class);
                        intentResponseValidator.validate(result);
                        return result;
                } catch (IOException exception) {
                        throw new IllegalStateException(
                                        "Claude returned JSON that could not be mapped to the response DTO",
                                        exception);
                }
        }

        /**
         * Read the HTTP response as String first, then parse it with the
         * Jackson 2 ObjectMapper used by this application. This avoids a
         * Jackson 2 / Jackson 3 HttpMessageConverter incompatibility in
         * Spring Boot 4 when asking RestClient to deserialize JsonNode.class.
         */

        private JsonNode postForJson(Map<String, Object> requestBody) {

                int maxAttempts = 4;

                for (int attempt = 1; attempt <= maxAttempts; attempt++) {

                        try {
                                String responseBody = claudeRestClient.post()
                                                .uri("/v1/messages")
                                                .body(requestBody)
                                                .retrieve()
                                                .body(String.class);

                                if (responseBody == null || responseBody.isBlank()) {
                                        throw new IllegalStateException(
                                                        "Empty response received from Claude");
                                }

                                return objectMapper.readTree(responseBody);

                        } catch (RestClientResponseException exception) {

                                if (exception.getStatusCode().value() != 429
                                                || attempt == maxAttempts) {
                                        throw exception;
                                }

                                long waitSeconds = getRetryDelay(exception, attempt);

                                System.out.println(
                                                "Claude rate limit reached. Retry "
                                                                + attempt + "/" + (maxAttempts - 1)
                                                                + " after " + waitSeconds + " seconds.");

                                try {
                                        Thread.sleep(waitSeconds * 1000);

                                } catch (InterruptedException interruptedException) {
                                        Thread.currentThread().interrupt();

                                        throw new IllegalStateException(
                                                        "Claude API retry interrupted",
                                                        interruptedException);
                                }

                        } catch (IOException exception) {

                                throw new IllegalStateException(
                                                "Unable to parse Claude API response as JSON",
                                                exception);
                        }
                }

                throw new IllegalStateException(
                                "Claude API request failed after maximum retries");
        }

        // Public wrapper for agentic tool calling
        public JsonNode sendMessagesRequest(
                        Map<String, Object> requestBody) {

                return postForJson(requestBody);
        }

        private long getRetryDelay(
                        RestClientResponseException exception,
                        int attempt) {

                String retryAfter = exception.getResponseHeaders() != null
                                ? exception.getResponseHeaders().getFirst("Retry-After")
                                : null;

                if (retryAfter != null) {
                        try {
                                return Math.max(1, Long.parseLong(retryAfter));
                        } catch (NumberFormatException ignored) {
                                // Use exponential backoff if the header is not numeric.
                        }
                }

                return Math.min(30L * attempt, 120L);
        }

        private String extractText(JsonNode response) {
                for (JsonNode block : response.path("content")) {
                        if ("text".equals(block.path("type").asText())) {
                                return block.path("text").asText("");
                        }
                }
                throw new IllegalStateException("Claude response did not contain a text block");
        }
}
