package com.vishnurp3.bookmanagementservice.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Schema(description = "Error response containing details about validation errors")
@Getter
public class ValidationErrorResponse extends ErrorResponse {

    @Schema(description = "Map of validation errors, where each key is a field name and the value is the error message",
            example = "{\"title\": \"must not be blank\", \"price\": \"must be greater than zero\"}")
    private final Map<String, String> errors;

    public ValidationErrorResponse(int statusCode, String message, LocalDateTime timestamp, Map<String, String> errors) {
        super(statusCode, message, timestamp);
        this.errors = errors;
    }
}
