package com.vishnurp3.bookmanagementservice.exception;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
public class ValidationErrorResponse extends ErrorResponse {

    private Map<String, String> errors;

    public ValidationErrorResponse(int statusCode, String message, LocalDateTime timestamp, Map<String, String> errors) {
        super(statusCode, message, timestamp);
        this.errors = errors;
    }
}
