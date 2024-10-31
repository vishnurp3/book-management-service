CREATE TABLE book
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    title            VARCHAR(255)       NOT NULL,
    author           VARCHAR(255)       NOT NULL,
    isbn             VARCHAR(20) UNIQUE NOT NULL,
    publication_date DATE,
    category         VARCHAR(100),
    description      TEXT,
    publisher        VARCHAR(255),
    price            DECIMAL(10, 2)
);
