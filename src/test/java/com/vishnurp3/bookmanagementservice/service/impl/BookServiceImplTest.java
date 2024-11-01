package com.vishnurp3.bookmanagementservice.service.impl;

import com.vishnurp3.bookmanagementservice.dto.BookRequestDto;
import com.vishnurp3.bookmanagementservice.dto.BookResponseDto;
import com.vishnurp3.bookmanagementservice.entity.Book;
import com.vishnurp3.bookmanagementservice.exception.DuplicateResourceException;
import com.vishnurp3.bookmanagementservice.exception.ResourceNotFoundException;
import com.vishnurp3.bookmanagementservice.mapper.BookMapper;
import com.vishnurp3.bookmanagementservice.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookServiceImpl bookService;


    @Nested
    @DisplayName("When creating a new book")
    class CreateBookTests {

        private BookRequestDto bookRequestDto;
        private Book bookEntity;
        private BookResponseDto bookResponseDto;

        @BeforeEach
        void init() {
            bookRequestDto = new BookRequestDto();
            bookRequestDto.setTitle("Effective Java");
            bookRequestDto.setAuthor("Joshua Bloch");
            bookRequestDto.setIsbn("9780134685991");
            bookRequestDto.setPublicationDate(LocalDate.of(2018, 1, 6));
            bookRequestDto.setCategory("Programming");
            bookRequestDto.setDescription("A comprehensive guide to Java best practices.");
            bookRequestDto.setPublisher("Addison-Wesley");
            bookRequestDto.setPrice(new BigDecimal("45.99"));

            bookEntity = Book.builder()
                    .id(1L)
                    .title("Effective Java")
                    .author("Joshua Bloch")
                    .isbn("9780134685991")
                    .publicationDate(LocalDate.of(2018, 1, 6))
                    .category("Programming")
                    .description("A comprehensive guide to Java best practices.")
                    .publisher("Addison-Wesley")
                    .price(new BigDecimal("45.99"))
                    .build();

            bookResponseDto = new BookResponseDto();
            bookResponseDto.setId(1L);
            bookResponseDto.setTitle("Effective Java");
            bookResponseDto.setAuthor("Joshua Bloch");
            bookResponseDto.setIsbn("9780134685991");
            bookResponseDto.setPublicationDate(LocalDate.of(2018, 1, 6));
            bookResponseDto.setCategory("Programming");
            bookResponseDto.setDescription("A comprehensive guide to Java best practices.");
            bookResponseDto.setPublisher("Addison-Wesley");
            bookResponseDto.setPrice(new BigDecimal("45.99"));
        }

        @Test
        @DisplayName("should successfully create a book when ISBN is unique")
        void shouldCreateBookWhenIsbnIsUnique() {
            when(bookRepository.existsByIsbn(bookRequestDto.getIsbn())).thenReturn(false);
            when(bookMapper.toEntity(bookRequestDto)).thenReturn(bookEntity);
            when(bookRepository.save(bookEntity)).thenReturn(bookEntity);
            when(bookMapper.toDto(bookEntity)).thenReturn(bookResponseDto);

            BookResponseDto result = bookService.createBook(bookRequestDto);

            assertNotNull(result);
            assertEquals("Effective Java", result.getTitle());
            assertEquals("Joshua Bloch", result.getAuthor());
            assertEquals("9780134685991", result.getIsbn());
            verify(bookRepository, times(1)).existsByIsbn("9780134685991");
            verify(bookRepository, times(1)).save(bookEntity);
        }

        @Test
        @DisplayName("should throw DuplicateResourceException when ISBN already exists")
        void shouldThrowExceptionWhenIsbnExists() {
            when(bookRepository.existsByIsbn(bookRequestDto.getIsbn())).thenReturn(true);

            DuplicateResourceException exception = assertThrows(
                    DuplicateResourceException.class,
                    () -> bookService.createBook(bookRequestDto)
            );

            assertEquals("Book with ISBN 9780134685991 already exists", exception.getMessage());
            verify(bookRepository, times(1)).existsByIsbn("9780134685991");
            verify(bookRepository, never()).save(any(Book.class));
        }

        @Test
        @DisplayName("should throw RuntimeException when database save fails")
        void shouldThrowRuntimeExceptionWhenDatabaseSaveFails() {
            when(bookRepository.existsByIsbn(bookRequestDto.getIsbn())).thenReturn(false);
            when(bookMapper.toEntity(bookRequestDto)).thenReturn(bookEntity);
            when(bookRepository.save(bookEntity)).thenThrow(new RuntimeException("Database is down"));

            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> bookService.createBook(bookRequestDto)
            );

            assertEquals("Database is down", exception.getMessage());
            verify(bookRepository, times(1)).existsByIsbn("9780134685991");
            verify(bookRepository, times(1)).save(bookEntity);
        }
    }

    @Nested
    @DisplayName("When updating an existing book")
    class UpdateBookTests {

        private BookRequestDto bookRequestDto;
        private Book existingBook;
        private Book updatedBook;
        private BookResponseDto bookResponseDto;

        @BeforeEach
        void setup() {
            bookRequestDto = new BookRequestDto();
            bookRequestDto.setTitle("Clean Code");
            bookRequestDto.setAuthor("Robert C. Martin");
            bookRequestDto.setIsbn("9780132350884");
            bookRequestDto.setPublicationDate(LocalDate.of(2008, 8, 1));
            bookRequestDto.setCategory("Programming");
            bookRequestDto.setDescription("A guide to writing clean code.");
            bookRequestDto.setPublisher("Prentice Hall");
            bookRequestDto.setPrice(new BigDecimal("33.99"));

            existingBook = Book.builder()
                    .id(1L)
                    .title("Effective Java")
                    .author("Joshua Bloch")
                    .isbn("9780134685991")
                    .publicationDate(LocalDate.of(2018, 1, 6))
                    .category("Programming")
                    .description("A comprehensive guide to Java best practices.")
                    .publisher("Addison-Wesley")
                    .price(new BigDecimal("45.99"))
                    .build();

            updatedBook = Book.builder()
                    .id(1L)
                    .title("Clean Code")
                    .author("Robert C. Martin")
                    .isbn("9780132350884")
                    .publicationDate(LocalDate.of(2008, 8, 1))
                    .category("Programming")
                    .description("A guide to writing clean code.")
                    .publisher("Prentice Hall")
                    .price(new BigDecimal("33.99"))
                    .build();

            bookResponseDto = new BookResponseDto();
            bookResponseDto.setId(1L);
            bookResponseDto.setTitle("Clean Code");
            bookResponseDto.setAuthor("Robert C. Martin");
            bookResponseDto.setIsbn("9780132350884");
            bookResponseDto.setPublicationDate(LocalDate.of(2008, 8, 1));
            bookResponseDto.setCategory("Programming");
            bookResponseDto.setDescription("A guide to writing clean code.");
            bookResponseDto.setPublisher("Prentice Hall");
            bookResponseDto.setPrice(new BigDecimal("33.99"));
        }

        @Test
        @DisplayName("should update book successfully when no ISBN conflict")
        void shouldUpdateBookSuccessfully() {
            when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
            when(bookRepository.existsByIsbn(bookRequestDto.getIsbn())).thenReturn(false);
            doNothing().when(bookMapper).updateEntityFromDto(bookRequestDto, existingBook);
            when(bookRepository.save(existingBook)).thenReturn(updatedBook);
            when(bookMapper.toDto(updatedBook)).thenReturn(bookResponseDto);

            BookResponseDto result = bookService.updateBook(1L, bookRequestDto);

            assertNotNull(result);
            assertEquals("Clean Code", result.getTitle());
            assertEquals("Robert C. Martin", result.getAuthor());
            verify(bookRepository, times(1)).findById(1L);
            verify(bookRepository, times(1)).save(existingBook);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when book ID does not exist")
        void shouldThrowExceptionWhenBookIdNotFound() {
            when(bookRepository.findById(1L)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> bookService.updateBook(1L, bookRequestDto)
            );

            assertEquals("Book not found with ID: 1", exception.getMessage());
            verify(bookRepository, times(1)).findById(1L);
            verify(bookRepository, never()).save(any(Book.class));
        }

        @Test
        @DisplayName("should throw DuplicateResourceException when new ISBN already exists")
        void shouldThrowExceptionWhenIsbnConflict() {
            when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
            when(bookRepository.existsByIsbn(bookRequestDto.getIsbn())).thenReturn(true);

            DuplicateResourceException exception = assertThrows(
                    DuplicateResourceException.class,
                    () -> bookService.updateBook(1L, bookRequestDto)
            );

            assertEquals("Another book with ISBN 9780132350884 already exists", exception.getMessage());
            verify(bookRepository, times(1)).findById(1L);
            verify(bookRepository, times(1)).existsByIsbn("9780132350884");
            verify(bookRepository, never()).save(any(Book.class));
        }

        @Test
        @DisplayName("should throw RuntimeException when database save fails")
        void shouldThrowRuntimeExceptionWhenDatabaseSaveFails() {
            when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
            when(bookRepository.existsByIsbn(bookRequestDto.getIsbn())).thenReturn(false);
            doNothing().when(bookMapper).updateEntityFromDto(bookRequestDto, existingBook);
            when(bookRepository.save(existingBook)).thenThrow(new RuntimeException("Database is down"));

            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> bookService.updateBook(1L, bookRequestDto)
            );

            assertEquals("Database is down", exception.getMessage());
            verify(bookRepository, times(1)).findById(1L);
            verify(bookRepository, times(1)).save(existingBook);
        }
    }

    @Nested
    @DisplayName("When fetching a book by ID")
    class GetBookByIdTests {

        private Book book;
        private BookResponseDto bookResponseDto;

        @BeforeEach
        void setup() {
            book = Book.builder()
                    .id(1L)
                    .title("Refactoring")
                    .author("Martin Fowler")
                    .isbn("9780134757599")
                    .publicationDate(LocalDate.of(2018, 11, 20))
                    .category("Software Engineering")
                    .description("Improving the design of existing code.")
                    .publisher("Addison-Wesley")
                    .price(new BigDecimal("47.99"))
                    .build();

            bookResponseDto = new BookResponseDto();
            bookResponseDto.setId(1L);
            bookResponseDto.setTitle("Refactoring");
            bookResponseDto.setAuthor("Martin Fowler");
            bookResponseDto.setIsbn("9780134757599");
            bookResponseDto.setPublicationDate(LocalDate.of(2018, 11, 20));
            bookResponseDto.setCategory("Software Engineering");
            bookResponseDto.setDescription("Improving the design of existing code.");
            bookResponseDto.setPublisher("Addison-Wesley");
            bookResponseDto.setPrice(new BigDecimal("47.99"));
        }

        @Test
        @DisplayName("should return BookResponseDto when book is found")
        void shouldReturnBookWhenBookIsFound() {
            when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
            when(bookMapper.toDto(book)).thenReturn(bookResponseDto);

            BookResponseDto result = bookService.getBookById(1L);

            assertNotNull(result);
            assertEquals("Refactoring", result.getTitle());
            assertEquals("Martin Fowler", result.getAuthor());
            assertEquals("9780134757599", result.getIsbn());
            verify(bookRepository, times(1)).findById(1L);
            verify(bookMapper, times(1)).toDto(book);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when book is not found")
        void shouldThrowExceptionWhenBookNotFound() {
            when(bookRepository.findById(1L)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> bookService.getBookById(1L)
            );

            assertEquals("Book not found with ID: 1", exception.getMessage());
            verify(bookRepository, times(1)).findById(1L);
            verify(bookMapper, never()).toDto(any(Book.class));
        }
    }

    @Nested
    @DisplayName("When deleting a book by ID")
    class DeleteBookTests {

        @Test
        @DisplayName("should delete book successfully when book exists")
        void shouldDeleteBookWhenBookExists() {
            Long validBookId = 1L;
            when(bookRepository.existsById(validBookId)).thenReturn(true);

            bookService.deleteBook(validBookId);

            verify(bookRepository, times(1)).existsById(validBookId);
            verify(bookRepository, times(1)).deleteById(validBookId);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when book does not exist")
        void shouldThrowExceptionWhenBookNotFound() {
            Long invalidBookId = 99L;
            when(bookRepository.existsById(invalidBookId)).thenReturn(false);

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> bookService.deleteBook(invalidBookId)
            );

            assertEquals("Book not found with ID: 99", exception.getMessage());
            verify(bookRepository, times(1)).existsById(invalidBookId);
            verify(bookRepository, never()).deleteById(anyLong());
        }
    }

    @Nested
    @DisplayName("When fetching books with filters")
    class GetBooksTests {

        private Pageable pageable;
        private Book book;
        private BookResponseDto bookResponseDto;
        private Page<Book> booksPage;

        @BeforeEach
        void init() {
            pageable = PageRequest.of(0, 10, Sort.by("title").ascending());

            book = Book.builder()
                    .id(1L)
                    .title("Clean Architecture")
                    .author("Robert C. Martin")
                    .isbn("9780134494166")
                    .publicationDate(LocalDate.of(2017, 9, 20))
                    .category("Software Engineering")
                    .description("A guide to software architecture.")
                    .publisher("Prentice Hall")
                    .price(new BigDecimal("40.99"))
                    .build();

            bookResponseDto = new BookResponseDto();
            bookResponseDto.setId(1L);
            bookResponseDto.setTitle("Clean Architecture");
            bookResponseDto.setAuthor("Robert C. Martin");
            bookResponseDto.setIsbn("9780134494166");
            bookResponseDto.setPublicationDate(LocalDate.of(2017, 9, 20));
            bookResponseDto.setCategory("Software Engineering");
            bookResponseDto.setDescription("A guide to software architecture.");
            bookResponseDto.setPublisher("Prentice Hall");
            bookResponseDto.setPrice(new BigDecimal("40.99"));

            booksPage = new PageImpl<>(List.of(book), pageable, 1);

        }

        @Test
        @DisplayName("should fetch books with title filter")
        void shouldFetchBooksWithTitleFilter() {
            when(bookRepository.findAll(any(), eq(pageable))).thenReturn(booksPage);
            when(bookMapper.toDto(book)).thenReturn(bookResponseDto);

            Page<BookResponseDto> result = bookService.getBooks("Clean Architecture", null, null, null, pageable);

            assertEquals(1, result.getTotalElements());
            assertEquals("Clean Architecture", result.getContent().get(0).getTitle());
            verify(bookRepository, times(1)).findAll(any(), eq(pageable));
        }

        @Test
        @DisplayName("should fetch books with multiple filters")
        void shouldFetchBooksWithMultipleFilters() {
            when(bookRepository.findAll(any(), eq(pageable))).thenReturn(booksPage);
            when(bookMapper.toDto(book)).thenReturn(bookResponseDto);

            Page<BookResponseDto> result = bookService.getBooks("Clean Architecture", "Robert C. Martin", "Software Engineering", "9780134494166", pageable);

            assertEquals(1, result.getTotalElements());
            assertEquals("Clean Architecture", result.getContent().get(0).getTitle());
            assertEquals("Robert C. Martin", result.getContent().get(0).getAuthor());
            verify(bookRepository, times(1)).findAll(any(), eq(pageable));
        }

        @Test
        @DisplayName("should fetch books with pagination applied")
        void shouldFetchBooksWithPagination() {
            Page<Book> multipleBooksPage = new PageImpl<>(List.of(book, book), pageable, 2);
            when(bookRepository.findAll(any(), eq(pageable))).thenReturn(multipleBooksPage);
            when(bookMapper.toDto(book)).thenReturn(bookResponseDto);

            Page<BookResponseDto> result = bookService.getBooks(null, null, null, null, pageable);

            assertEquals(2, result.getTotalElements());
            assertEquals(2, result.getContent().size());
            verify(bookRepository, times(1)).findAll(any(), eq(pageable));
        }

        @Test
        @DisplayName("should return empty page when no books match filter")
        void shouldReturnEmptyPageWhenNoBooksMatch() {
            Page<Book> emptyBooksPage = new PageImpl<>(List.of(), pageable, 0);
            when(bookRepository.findAll(any(), eq(pageable))).thenReturn(emptyBooksPage);

            Page<BookResponseDto> result = bookService.getBooks("Nonexistent Title", null, null, null, pageable);

            assertEquals(0, result.getTotalElements());
            verify(bookRepository, times(1)).findAll(any(), eq(pageable));
        }
    }
}
