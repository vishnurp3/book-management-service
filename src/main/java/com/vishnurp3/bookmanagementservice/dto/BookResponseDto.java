package com.vishnurp3.bookmanagementservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Data transfer object for book details in API responses")
@Data
public class BookResponseDto {

    @Schema(description = "The unique identifier of the book", example = "1")
    private Long id;
    @Schema(description = "The title of the book", example = "Effective Java")
    private String title;
    @Schema(description = "The author of the book", example = "Joshua Bloch")
    private String author;
    @Schema(description = "The ISBN of the book", example = "9780134685991")
    private String isbn;
    @Schema(description = "The publication date of the book in YYYY-MM-DD format", example = "2018-01-06")
    private LocalDate publicationDate;
    @Schema(description = "The category or genre of the book", example = "Programming")
    private String category;
    @Schema(description = "A brief description of the book", example = "A comprehensive guide to Java programming best practices")
    private String description;
    @Schema(description = "The publisher of the book", example = "Addison-Wesley")
    private String publisher;
    @Schema(description = "The price of the book", example = "39.99")
    private BigDecimal price;
    @Schema(description = "The date the book record was created", example = "2023-01-01")
    private LocalDate createdAt;
    @Schema(description = "The date the book record was last updated", example = "2023-05-10")
    private LocalDate updatedAt;
}
