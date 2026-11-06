package com.workmate.backend.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ClaudeChatRequest(

        @NotBlank(message = "Message cannot be empty") String message

) {

}
