package com.vishnurp3.bookmanagementservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BookManagementServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookManagementServiceApplication.class, args);
    }

}
