
package com.workmate.backend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.workmate.backend.dto.evaluation.IntentTestCase;
import com.workmate.backend.dto.evaluation.IntentEvaluationResult;
import com.workmate.backend.dto.response.IntentClassificationResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class IntentEvaluationService {

        private final ObjectMapper objectMapper;
        private final ClaudeService claudeService;
        private final long delayBetweenRequestsMs;

        public IntentEvaluationService(
                        ObjectMapper objectMapper,
                        ClaudeService claudeService,
                        @Value("${evaluation.delay-ms:15000}") long delayBetweenRequestsMs) {

                this.objectMapper = objectMapper;
                this.claudeService = claudeService;
                this.delayBetweenRequestsMs = delayBetweenRequestsMs;
        }

        public List<IntentEvaluationResult> evaluate() throws Exception {

                List<IntentTestCase> testCases = objectMapper.readValue(
                                new ClassPathResource(
                                                "evaluation/intent-test-cases.json").getInputStream(),
                                new TypeReference<List<IntentTestCase>>() {
                                });

                List<IntentEvaluationResult> results = new ArrayList<>();

                for (int i = 0; i < testCases.size(); i++) {

                        IntentTestCase testCase = testCases.get(i);

                        System.out.println(
                                        "Evaluating test case " + (i + 1)
                                                        + "/" + testCases.size()
                                                        + ": " + testCase.id());

                        if (i > 0) {
                                Thread.sleep(delayBetweenRequestsMs);
                        }

                        IntentClassificationResponse prediction = claudeService.classifyRequest(testCase.input());

                        boolean correct = prediction.intent() == testCase.expectedIntent();

                        results.add(new IntentEvaluationResult(
                                        testCase.id(),
                                        testCase.input(),
                                        testCase.expectedIntent(),
                                        prediction.intent(),
                                        correct));
                }

                return results;
        }
}
