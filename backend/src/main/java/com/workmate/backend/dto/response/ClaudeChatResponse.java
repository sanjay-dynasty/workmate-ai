package com.workmate.backend.dto.response;

public record ClaudeChatResponse(

        String response,
        String model,
        String stopReason

) {

}