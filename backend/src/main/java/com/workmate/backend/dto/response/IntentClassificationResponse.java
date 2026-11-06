package com.workmate.backend.dto.response;

import com.workmate.backend.dto.Intent;

import java.math.BigDecimal;

public record IntentClassificationResponse(

        Intent intent,

        BigDecimal confidence,

        boolean requiresTool,

        String response,

        TaskEntities entities

) {
}