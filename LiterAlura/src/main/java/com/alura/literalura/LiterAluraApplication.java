package com.alura.literalura;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import repository.AuthorRepository;
import repository.BooksRepository;

import Main.Main;


@SpringBootApplication
public class LiterAluraApplication  implements CommandLineRunner {
    @Autowired
    private BooksRepository bookRepository;
    @Autowired
    private AuthorRepository authorRepository;

    public static void main(String[] args) {
        SpringApplication.run(LiterAluraApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Main init = new Main(bookRepository, authorRepository);
        init.showMenu();
    }
}



