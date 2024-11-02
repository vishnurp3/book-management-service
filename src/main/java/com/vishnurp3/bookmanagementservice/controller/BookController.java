package com.vishnurp3.bookmanagementservice.controller;

import com.vishnurp3.bookmanagementservice.dto.BookRequestDto;
import com.vishnurp3.bookmanagementservice.dto.BookResponseDto;
import com.vishnurp3.bookmanagementservice.exception.ErrorResponse;
import com.vishnurp3.bookmanagementservice.exception.ValidationErrorResponse;
import com.vishnurp3.bookmanagementservice.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Book Management", description = "API for managing books")
@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @Operation(
            summary = "Create a new book",
            description = "Adds a new book to the database. The ISBN must be unique and is used to identify the book."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Book created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Book with the provided ISBN already exists",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponseDto createBook(@Valid @RequestBody BookRequestDto bookRequestDto) {
        return bookService.createBook(bookRequestDto);
    }

    @Operation(
            summary = "Retrieve a book by ID",
            description = "Fetches the details of a book specified by its unique ID. Returns a 404 error if the book is not found."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Book retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Book not found with the specified ID",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookResponseDto getBookById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    @Operation(
            summary = "Update an existing book",
            description = "Updates the details of a book specified by its unique ID. The book's ISBN must be unique and cannot match another book's ISBN."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Book updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Book not found with the specified ID",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Another book with the provided ISBN already exists",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorResponse.class))
            )
    })
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookResponseDto updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookRequestDto bookRequestDto) {
        return bookService.updateBook(id, bookRequestDto);
    }

    @Operation(
            summary = "Delete a book by ID",
            description = "Deletes a book from the database specified by its unique ID. Returns a 404 error if the book is not found."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Book deleted successfully (no content returned)"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Book not found with the specified ID",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
    }

    @Operation(
            summary = "Retrieve a paginated list of books with optional filters",
            description = "Fetches books based on optional filters for title, author, category, and ISBN. Supports pagination, sorting, and filtering."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of books retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookResponseDto.class))
            )
    })
    @Parameters({
            @Parameter(name = "title", description = "Filter books by title", example = "Effective Java"),
            @Parameter(name = "author", description = "Filter books by author", example = "Joshua Bloch"),
            @Parameter(name = "category", description = "Filter books by category", example = "Programming"),
            @Parameter(name = "isbn", description = "Filter books by ISBN", example = "9780134685991"),
            @Parameter(name = "page", description = "Page number for pagination (0-indexed)", example = "0", schema = @Schema(type = "integer")),
            @Parameter(name = "size", description = "Number of records per page", example = "10", schema = @Schema(type = "integer")),
            @Parameter(name = "sortBy", description = "Field to sort by", example = "title"),
            @Parameter(name = "sortDir", description = "Sort direction, either 'asc' or 'desc'", example = "asc")
    })
    @GetMapping
    public Page<BookResponseDto> getBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String isbn,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Sort sort = Sort.by(
                sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                sortBy
        );
        Pageable pageable = PageRequest.of(page, size, sort);
        return bookService.getBooks(title, author, category, isbn, pageable);
    }
}
