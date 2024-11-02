
# Book Management API

A RESTful API for managing a collection of books, built with Spring Boot 3, Java 17, and MySQL. 
This API supports CRUD operations, as well as filtering, pagination, and sorting.

## Features

- Add, update, delete, and retrieve books
- Filter, paginate, and sort book listings
- Comprehensive error handling
- Ready for JWT-based security integration

## Technologies

- **Java 17**
- **Spring Boot 3**
- **MySQL** (production database)
- **H2 Database** (in-memory for testing)
- **MapStruct** (DTO mapping)
- **Lombok** (for reducing boilerplate code)
- **Swagger/OpenAPI** (for API documentation)

## Prerequisites

- **Java 17** or later
- **Maven** (for building and managing dependencies)
- **MySQL** (for production database)

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/vishnurp3/book-management-service.git
cd book-management-api
```

### 2. Configure the Database

Set up your MySQL database and update the connection settings in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/book_management_db
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password
```

### 3. Run the Application

You can run the application using Maven:

```bash
mvn spring-boot:run
```

The application will start on **http://localhost:8080** by default.

## API Documentation

API documentation is available via Swagger. Visit **http://localhost:8080/swagger-ui.html** to view and test the endpoints.

### Sample Endpoints

- **GET /api/v1/books** - Retrieve a list of books (supports pagination, sorting, and filtering)
- **POST /api/v1/books** - Add a new book
- **PUT /api/v1/books/{id}** - Update an existing book by ID
- **DELETE /api/v1/books/{id}** - Delete a book by ID

## Example Request

### Add a Book

```http
POST /api/v1/books
Content-Type: application/json

{
  "title": "The Pragmatic Programmer",
  "author": "Andrew Hunt and David Thomas",
  "isbn": "9780201616224",
  "publicationDate": "1999-10-20",
  "category": "Software Engineering",
  "description": "Classic book on programming practices and principles",
  "publisher": "Addison-Wesley",
  "price": 49.99
}
```

## Testing

The project includes unit and integration tests. To run tests, use:

```bash
mvn test
```

## Future Enhancements

- Implement JWT-based security for secure access to endpoints
- Add user roles for finer-grained access control
- Expand filtering and sorting capabilities

## Contributing

Contributions are welcome! Please fork the repository and create a pull request.

## License

This project is licensed under the MIT License.
