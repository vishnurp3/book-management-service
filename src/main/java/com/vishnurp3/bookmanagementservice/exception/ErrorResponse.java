package com.vishnurp3.bookmanagementservice.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "Standard error response containing status code, message, and timestamp")
@Data
@AllArgsConstructor
public class ErrorResponse {

    @Schema(description = "HTTP status code of the error", example = "404")
    private int statusCode;

    @Schema(description = "Error message describing the issue", example = "Book not found with ID: 1")
    private String message;

    @Schema(description = "Timestamp when the error occurred", example = "2023-11-02T15:20:00")
    private LocalDateTime timestamp;
}
