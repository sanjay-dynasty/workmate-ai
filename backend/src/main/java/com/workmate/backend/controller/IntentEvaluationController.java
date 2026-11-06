package com.workmate.backend.controller;

import com.workmate.backend.dto.evaluation.IntentEvaluationResult;
import com.workmate.backend.service.IntentEvaluationService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claude/evaluation")
public class IntentEvaluationController {

    private final IntentEvaluationService evaluationService;

    public IntentEvaluationController(
            IntentEvaluationService evaluationService) {

        this.evaluationService = evaluationService;
    }

    @PostMapping("/run")
    public List<IntentEvaluationResult> runEvaluation()
            throws Exception {

        return evaluationService.evaluate();
    }
}