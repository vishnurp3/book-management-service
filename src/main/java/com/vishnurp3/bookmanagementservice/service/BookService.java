package com.vishnurp3.bookmanagementservice.service;

import com.vishnurp3.bookmanagementservice.dto.BookRequestDto;
import com.vishnurp3.bookmanagementservice.dto.BookResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {

    BookResponseDto createBook(BookRequestDto bookRequestDto);

    BookResponseDto updateBook(Long id, BookRequestDto bookRequestDto);

    BookResponseDto getBookById(Long id);

    void deleteBook(Long id);

    Page<BookResponseDto> getBooks(String title, String author, String category, String isbn, Pageable pageable);
}
