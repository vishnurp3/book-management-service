package com.vishnurp3.bookmanagementservice.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BookRequestDto {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Author is required")
    private String author;

    @NotBlank(message = "ISBN is required")
    @Pattern(regexp = "\\d{13}", message = "ISBN must be a 13-digit number")
    private String isbn;

    @PastOrPresent(message = "Publication date cannot be in the future")
    private LocalDate publicationDate;

    private String category;

    private String description;

    private String publisher;

    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal price;
}
