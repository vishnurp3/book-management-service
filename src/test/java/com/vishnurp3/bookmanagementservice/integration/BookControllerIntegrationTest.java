package com.vishnurp3.bookmanagementservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vishnurp3.bookmanagementservice.dto.BookRequestDto;
import com.vishnurp3.bookmanagementservice.entity.Book;
import com.vishnurp3.bookmanagementservice.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("SqlResolve")
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void cleanDatabase() {
        bookRepository.deleteAll();
    }

    @Nested
    class CreateBookTests {

        private BookRequestDto bookRequestDto;

        @BeforeEach
        void setUp() {
            bookRepository.deleteAll();
            bookRequestDto = new BookRequestDto();
            bookRequestDto.setTitle("The Great Gatsby");
            bookRequestDto.setAuthor("F. Scott Fitzgerald");
            bookRequestDto.setIsbn("9780743273565");
            bookRequestDto.setPublicationDate(LocalDate.of(1925, 4, 10));
            bookRequestDto.setCategory("Fiction");
            bookRequestDto.setDescription("A novel about the American dream and the roaring twenties.");
            bookRequestDto.setPublisher("Scribner");
            bookRequestDto.setPrice(new BigDecimal("10.99"));
        }

        @Test
        void shouldCreateBookSuccessfully() throws Exception {
            mockMvc.perform(post("/api/v1/books")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bookRequestDto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.title", is("The Great Gatsby")))
                    .andExpect(jsonPath("$.isbn", is("9780743273565")));
        }

        @Test
        void shouldReturnBadRequestForDuplicateIsbn() throws Exception {
            mockMvc.perform(post("/api/v1/books")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bookRequestDto)))
                    .andExpect(status().isCreated());

            mockMvc.perform(post("/api/v1/books")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bookRequestDto)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message", containsString("already exists")));
        }

        @Test
        void shouldReturnBadRequestWhenRequiredFieldsAreMissing() throws Exception {
            bookRequestDto.setTitle(null);
            mockMvc.perform(post("/api/v1/books")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bookRequestDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.title", containsString("Title is required")));
        }

        @Test
        void shouldReturnBadRequestForInvalidIsbnFormat() throws Exception {
            bookRequestDto.setIsbn("invalid_isbn");
            mockMvc.perform(post("/api/v1/books")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bookRequestDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.isbn", containsString("must be a 13-digit number")));
        }

        @Test
        void shouldReturnBadRequestForFuturePublicationDate() throws Exception {
            bookRequestDto.setPublicationDate(LocalDate.now().plusDays(1));
            mockMvc.perform(post("/api/v1/books")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bookRequestDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.publicationDate", containsString("cannot be in the future")));
        }

        @Test
        void shouldReturnBadRequestForNonPositivePrice() throws Exception {
            bookRequestDto.setPrice(BigDecimal.ZERO);
            mockMvc.perform(post("/api/v1/books")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bookRequestDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.price", containsString("must be greater than zero")));
        }

        @Test
        void shouldCreateBookWithMinimalRequiredData() throws Exception {
            BookRequestDto minimalDto = new BookRequestDto();
            minimalDto.setTitle("Minimal Book");
            minimalDto.setAuthor("Minimal Author");
            minimalDto.setIsbn("9781234567890");
            minimalDto.setPublicationDate(LocalDate.of(2000, 1, 1));
            minimalDto.setPrice(new BigDecimal("5.99"));

            mockMvc.perform(post("/api/v1/books")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(minimalDto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.title", is("Minimal Book")))
                    .andExpect(jsonPath("$.isbn", is("9781234567890")));
        }
    }

    @Nested
    class GetBookByIdTests {

        private Book savedBook;

        @BeforeEach
        void setUp() {
            Book book = Book.builder()
                    .title("To Kill a Mockingbird")
                    .author("Harper Lee")
                    .isbn("9780061120084")
                    .publicationDate(LocalDate.of(1960, 7, 11))
                    .category("Fiction")
                    .description("A novel about racial injustice in the American South.")
                    .publisher("J.B. Lippincott & Co.")
                    .price(new BigDecimal("7.99"))
                    .build();

            savedBook = bookRepository.save(book);
        }

        @Test
        void shouldRetrieveBookByIdSuccessfully() throws Exception {
            mockMvc.perform(get("/api/v1/books/" + savedBook.getId())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(savedBook.getId().intValue())))
                    .andExpect(jsonPath("$.title", is("To Kill a Mockingbird")))
                    .andExpect(jsonPath("$.author", is("Harper Lee")))
                    .andExpect(jsonPath("$.isbn", is("9780061120084")))
                    .andExpect(jsonPath("$.category", is("Fiction")))
                    .andExpect(jsonPath("$.description", is("A novel about racial injustice in the American South.")))
                    .andExpect(jsonPath("$.publisher", is("J.B. Lippincott & Co.")))
                    .andExpect(jsonPath("$.price", is(7.99)));
        }

        @Test
        void shouldReturnNotFoundForNonExistentBook() throws Exception {
            long nonExistentId = savedBook.getId() + 100;
            mockMvc.perform(get("/api/v1/books/" + nonExistentId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message", is("Book not found with ID: " + nonExistentId)));
        }
    }

    @Nested
    class UpdateBookTests {

        private Book existingBook;
        private Book anotherBook;
        private BookRequestDto updateRequestDto;

        @BeforeEach
        void setUp() {
            existingBook = bookRepository.save(Book.builder()
                    .title("The Catcher in the Rye")
                    .author("J.D. Salinger")
                    .isbn("9780316769488")
                    .publicationDate(LocalDate.of(1951, 7, 16))
                    .category("Fiction")
                    .description("A novel about teenage rebellion.")
                    .publisher("Little, Brown and Company")
                    .price(new BigDecimal("6.99"))
                    .build());

            anotherBook = bookRepository.save(Book.builder()
                    .title("To Kill a Mockingbird")
                    .author("Harper Lee")
                    .isbn("9780061120084")
                    .publicationDate(LocalDate.of(1960, 7, 11))
                    .category("Fiction")
                    .description("A novel about racial injustice.")
                    .publisher("J.B. Lippincott & Co.")
                    .price(new BigDecimal("7.99"))
                    .build());

            updateRequestDto = new BookRequestDto();
            updateRequestDto.setTitle("The Catcher in the Rye - Updated");
            updateRequestDto.setAuthor("J.D. Salinger");
            updateRequestDto.setIsbn("9780316769488");
            updateRequestDto.setPublicationDate(LocalDate.of(1951, 7, 16));
            updateRequestDto.setCategory("Classic Fiction");
            updateRequestDto.setDescription("A novel about teenage rebellion, updated edition.");
            updateRequestDto.setPublisher("Little, Brown and Company");
            updateRequestDto.setPrice(new BigDecimal("8.99"));
        }

        @Test
        void shouldUpdateBookSuccessfully() throws Exception {
            mockMvc.perform(put("/api/v1/books/" + existingBook.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequestDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title", is("The Catcher in the Rye - Updated")))
                    .andExpect(jsonPath("$.category", is("Classic Fiction")))
                    .andExpect(jsonPath("$.price", is(8.99)));
        }

        @Test
        void shouldReturnNotFoundForNonExistentBookId() throws Exception {
            long nonExistentId = existingBook.getId() + 100;
            mockMvc.perform(put("/api/v1/books/" + nonExistentId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequestDto)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message", containsString("Book not found with ID: " + nonExistentId)));
        }

        @Test
        void shouldReturnBadRequestForDuplicateIsbn() throws Exception {
            updateRequestDto.setIsbn(anotherBook.getIsbn());

            mockMvc.perform(put("/api/v1/books/" + existingBook.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequestDto)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message", containsString("Another book with ISBN " + anotherBook.getIsbn() + " already exists")));
        }

        @Test
        void shouldReturnBadRequestForInvalidIsbnFormat() throws Exception {
            updateRequestDto.setIsbn("invalid_isbn");

            mockMvc.perform(put("/api/v1/books/" + existingBook.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequestDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.isbn", containsString("must be a 13-digit number")));
        }

        @Test
        void shouldReturnBadRequestForFuturePublicationDate() throws Exception {
            updateRequestDto.setPublicationDate(LocalDate.now().plusDays(1));

            mockMvc.perform(put("/api/v1/books/" + existingBook.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequestDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.publicationDate", containsString("cannot be in the future")));
        }

        @Test
        void shouldReturnBadRequestForMissingRequiredFields() throws Exception {
            updateRequestDto.setTitle(null);

            mockMvc.perform(put("/api/v1/books/" + existingBook.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequestDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.title", containsString("Title is required")));
        }
    }

    @Nested
    class DeleteBookTests {

        private Book existingBook;

        @BeforeEach
        void setUp() {
            existingBook = bookRepository.save(Book.builder()
                    .title("Pride and Prejudice")
                    .author("Jane Austen")
                    .isbn("9780141439518")
                    .publicationDate(LocalDate.of(1813, 1, 28))
                    .category("Classic Literature")
                    .description("A novel about love and societal expectations in 19th century England.")
                    .publisher("Penguin Classics")
                    .price(new BigDecimal("5.99"))
                    .build());
        }

        @Test
        void shouldDeleteBookSuccessfully() throws Exception {
            mockMvc.perform(delete("/api/v1/books/" + existingBook.getId())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());

            mockMvc.perform(get("/api/v1/books/" + existingBook.getId())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message", containsString("Book not found with ID: " + existingBook.getId())));
        }

        @Test
        void shouldReturnNotFoundForNonExistentBookId() throws Exception {
            long nonExistentId = existingBook.getId() + 100;

            mockMvc.perform(delete("/api/v1/books/" + nonExistentId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message", containsString("Book not found with ID: " + nonExistentId)));
        }
    }

    @Nested
    class GetBooksTests {

        @BeforeEach
        void setUp() {
            bookRepository.save(Book.builder()
                    .title("The Catcher in the Rye")
                    .author("J.D. Salinger")
                    .isbn("9780316769488")
                    .publicationDate(LocalDate.of(1951, 7, 16))
                    .category("Fiction")
                    .description("A novel about teenage rebellion.")
                    .publisher("Little, Brown and Company")
                    .price(new BigDecimal("6.99"))
                    .build());

            bookRepository.save(Book.builder()
                    .title("To Kill a Mockingbird")
                    .author("Harper Lee")
                    .isbn("9780061120084")
                    .publicationDate(LocalDate.of(1960, 7, 11))
                    .category("Fiction")
                    .description("A novel about racial injustice.")
                    .publisher("J.B. Lippincott & Co.")
                    .price(new BigDecimal("7.99"))
                    .build());

            bookRepository.save(Book.builder()
                    .title("1984")
                    .author("George Orwell")
                    .isbn("9780451524935")
                    .publicationDate(LocalDate.of(1949, 6, 8))
                    .category("Dystopian Fiction")
                    .description("A dystopian novel set in a totalitarian society.")
                    .publisher("Secker & Warburg")
                    .price(new BigDecimal("9.99"))
                    .build());
        }

        @Test
        void shouldRetrieveAllBooksWithDefaultPagination() throws Exception {
            mockMvc.perform(get("/api/v1/books")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(3)))
                    .andExpect(jsonPath("$.content[0].title", is("The Catcher in the Rye")))
                    .andExpect(jsonPath("$.content[1].title", is("To Kill a Mockingbird")))
                    .andExpect(jsonPath("$.content[2].title", is("1984")));
        }

        @Test
        void shouldRetrieveBooksByTitle() throws Exception {
            mockMvc.perform(get("/api/v1/books")
                            .param("title", "1984")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].title", is("1984")));
        }

        @Test
        void shouldRetrieveBooksByAuthor() throws Exception {
            mockMvc.perform(get("/api/v1/books")
                            .param("author", "Harper Lee")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].author", is("Harper Lee")))
                    .andExpect(jsonPath("$.content[0].title", is("To Kill a Mockingbird")));
        }

        @Test
        void shouldRetrieveBooksByCategory() throws Exception {
            mockMvc.perform(get("/api/v1/books")
                            .param("category", "Fiction")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(3)));
        }

        @Test
        void shouldRetrieveBooksByIsbn() throws Exception {
            mockMvc.perform(get("/api/v1/books")
                            .param("isbn", "9780061120084")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].isbn", is("9780061120084")))
                    .andExpect(jsonPath("$.content[0].title", is("To Kill a Mockingbird")));
        }

        @Test
        void shouldRetrieveBooksByCombinedFilters() throws Exception {
            mockMvc.perform(get("/api/v1/books")
                            .param("title", "1984")
                            .param("author", "George Orwell")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].title", is("1984")))
                    .andExpect(jsonPath("$.content[0].author", is("George Orwell")));
        }

        @Test
        void shouldRetrieveBooksWithPaginationAndSorting() throws Exception {
            mockMvc.perform(get("/api/v1/books")
                            .param("page", "0")
                            .param("size", "2")
                            .param("sortBy", "title")
                            .param("sortDir", "asc")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(2)))
                    .andExpect(jsonPath("$.content[0].title", is("1984")))
                    .andExpect(jsonPath("$.content[1].title", is("The Catcher in the Rye")))
                    .andExpect(jsonPath("$.totalElements", is(3)))
                    .andExpect(jsonPath("$.totalPages", is(2)));
        }
    }
}
