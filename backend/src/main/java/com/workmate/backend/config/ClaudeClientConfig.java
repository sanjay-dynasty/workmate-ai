package com.workmate.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ClaudeClientConfig {

    @Bean
    public RestClient claudeRestClient(
            @Value("${anthropic.base-url}") String baseUrl,
            @Value("${anthropic.api-key}") String apiKey) {

        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("x-api-key", apiKey)
                .defaultHeader("anthropic-version", "2023-06-01")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

}
