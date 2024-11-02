package com.vishnurp3.bookmanagementservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Data transfer object for creating or updating a book")
@Data
public class BookRequestDto {

    @NotBlank(message = "Title is required")
    @Schema(description = "The title of the book", example = "Effective Java")
    private String title;

    @NotBlank(message = "Author is required")
    @Schema(description = "The author of the book", example = "Joshua Bloch")
    private String author;

    @NotBlank(message = "ISBN is required")
    @Pattern(regexp = "\\d{13}", message = "ISBN must be a 13-digit number")
    @Schema(description = "The ISBN of the book, must be 13 characters", example = "9780134685991")
    private String isbn;

    @PastOrPresent(message = "Publication date cannot be in the future")
    @Schema(description = "The publication date of the book in YYYY-MM-DD format", example = "2018-01-06")
    private LocalDate publicationDate;

    @Schema(description = "The category or genre of the book", example = "Programming")
    private String category;

    @Schema(description = "A brief description of the book", example = "A comprehensive guide to Java programming best practices")
    private String description;

    @Schema(description = "The publisher of the book", example = "Addison-Wesley")
    private String publisher;

    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
    @Digits(integer = 10, fraction = 2)
    @Schema(description = "The price of the book", example = "39.99")
    private BigDecimal price;
}
