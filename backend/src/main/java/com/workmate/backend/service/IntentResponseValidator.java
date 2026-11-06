package com.workmate.backend.service;

import com.workmate.backend.dto.response.IntentClassificationResponse;

import org.springframework.stereotype.Service;

@Service
public class IntentResponseValidator {

    public void validate(
            IntentClassificationResponse response) {

        if (response == null) {
            throw new IllegalArgumentException("Classification response cannot be null");
        }

        if (response.intent() == null) {
            throw new IllegalArgumentException(
                    "Intent cannot be null");
        }

        if (response.confidence() == null
                || response.confidence().compareTo(
                        java.math.BigDecimal.ZERO) < 0
                || response.confidence().compareTo(
                        java.math.BigDecimal.ONE) > 0) {

            throw new IllegalArgumentException(
                    "Confidence must be between 0 and 1");
        }

        if (response.response() == null
                || response.response().isBlank()) {

            throw new IllegalArgumentException(
                    "Response cannot be empty");
        }

        if (response.entities() == null) {
            throw new IllegalArgumentException(
                    "Entities cannot be null");
        }
    }
}