package com.workmate.backend.dto.request;

import java.util.Map;

public record ToolExecutionRequest(
        String toolName,
        Map<String, Object> input

)

{
}