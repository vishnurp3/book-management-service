CREATE TABLE books
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    title            VARCHAR(255) NOT NULL,
    author           VARCHAR(255) NOT NULL,
    isbn             VARCHAR(13)  NOT NULL UNIQUE,
    publication_date DATE,
    category         VARCHAR(100),
    description      TEXT,
    publisher        VARCHAR(255),
    price            DECIMAL(12, 2) CHECK (price > 0),
    created_at       DATE         NOT NULL DEFAULT CURRENT_DATE,
    updated_at       DATE                  DEFAULT NULL
);
