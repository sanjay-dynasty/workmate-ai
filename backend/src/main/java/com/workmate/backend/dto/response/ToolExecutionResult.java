package com.workmate.backend.dto.response;

public record ToolExecutionResult(

        String toolName,
        boolean success,
        Object data,
        String error

) {
    public static ToolExecutionResult success(String toolName, Object data) {

        return new ToolExecutionResult(toolName, true, data, null);
    }

    public static ToolExecutionResult failure(String toolName, String error) {
        return new ToolExecutionResult(toolName,
                false,
                null,
                error);
    }
}
