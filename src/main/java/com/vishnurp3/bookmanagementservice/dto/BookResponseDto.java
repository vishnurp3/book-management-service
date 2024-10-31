package com.vishnurp3.bookmanagementservice.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BookResponseDto {

    private Long id;
    private String title;
    private String author;
    private String isbn;
    private LocalDate publicationDate;
    private String category;
    private String description;
    private String publisher;
    private BigDecimal price;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}
