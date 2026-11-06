package com.workmate.backend.dto.evaluation;

import com.workmate.backend.dto.Intent;

public record IntentEvaluationResult(

        int testId,

        String input,

        Intent expectedIntent,

        Intent predictedIntent,

        boolean correct

) {
}