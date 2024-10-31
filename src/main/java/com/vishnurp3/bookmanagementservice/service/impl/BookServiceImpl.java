package com.vishnurp3.bookmanagementservice.service.impl;

import com.vishnurp3.bookmanagementservice.dto.BookRequestDto;
import com.vishnurp3.bookmanagementservice.dto.BookResponseDto;
import com.vishnurp3.bookmanagementservice.entity.Book;
import com.vishnurp3.bookmanagementservice.exception.DuplicateResourceException;
import com.vishnurp3.bookmanagementservice.exception.ResourceNotFoundException;
import com.vishnurp3.bookmanagementservice.mapper.BookMapper;
import com.vishnurp3.bookmanagementservice.repository.BookRepository;
import com.vishnurp3.bookmanagementservice.service.BookService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    @Transactional
    public BookResponseDto createBook(BookRequestDto bookRequestDto) {
        log.info("Attempting to create a new book with ISBN: {}", bookRequestDto.getIsbn());

        if (bookRepository.existsByIsbn(bookRequestDto.getIsbn())) {
            log.warn("Creation failed: Book with ISBN {} already exists", bookRequestDto.getIsbn());
            throw new DuplicateResourceException("Book with ISBN " + bookRequestDto.getIsbn() + " already exists");
        }

        Book book = bookMapper.toEntity(bookRequestDto);
        Book savedBook = bookRepository.save(book);

        log.info("Book created successfully with ID: {}", savedBook.getId());
        return bookMapper.toDto(savedBook);
    }

    @Override
    @Transactional
    public BookResponseDto updateBook(Long id, BookRequestDto bookRequestDto) {
        log.info("Attempting to update book with ID: {}", id);

        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + id));

        if (!existingBook.getIsbn().equals(bookRequestDto.getIsbn())
                && bookRepository.existsByIsbn(bookRequestDto.getIsbn())) {
            log.warn("Update failed: Another book with ISBN {} already exists", bookRequestDto.getIsbn());
            throw new DuplicateResourceException("Another book with ISBN " + bookRequestDto.getIsbn() + " already exists");
        }

        bookMapper.updateEntityFromDto(bookRequestDto, existingBook);
        Book updatedBook = bookRepository.save(existingBook);

        log.info("Book updated successfully with ID: {}", updatedBook.getId());
        return bookMapper.toDto(updatedBook);
    }

    @Override
    public BookResponseDto getBookById(Long id) {
        log.info("Fetching book with ID: {}", id);

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + id));

        return bookMapper.toDto(book);
    }

    @Override
    @Transactional
    public void deleteBook(Long id) {
        log.info("Attempting to delete book with ID: {}", id);

        if (!bookRepository.existsById(id)) {
            log.warn("Deletion failed: Book not found with ID: {}", id);
            throw new ResourceNotFoundException("Book not found with ID: " + id);
        }

        bookRepository.deleteById(id);
        log.info("Book deleted successfully with ID: {}", id);
    }


    @Override
    public Page<BookResponseDto> getBooks(String title, String author, String category, String isbn, Pageable pageable) {
        log.info("Fetching books with filters - Title: {}, Author: {}, Category: {}, ISBN: {}", title, author, category, isbn);

        Book probe = new Book();
        probe.setTitle(title);
        probe.setAuthor(author);
        probe.setCategory(category);
        probe.setIsbn(isbn);

        ExampleMatcher matcher = ExampleMatcher.matchingAll()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

        Example<Book> example = Example.of(probe, matcher);

        Page<Book> booksPage = bookRepository.findAll(example, pageable);
        return booksPage.map(bookMapper::toDto);
    }
}
