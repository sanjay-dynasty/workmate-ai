package com.workmate.backend.dto.evaluation;

import com.workmate.backend.dto.Intent;

public record IntentTestCase(
        int id,
        String input,
        Intent expectedIntent

) {
}