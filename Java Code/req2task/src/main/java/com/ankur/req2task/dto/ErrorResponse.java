package com.ankur.req2task.dto;

/**
 * Structured error response. Never leak stack traces to the caller.
 */
public record ErrorResponse(
        boolean success,
        String error,
        String detail) {
    public static ErrorResponse of(String error, String detail) {
        return new ErrorResponse(false, error, detail);
    }
}
