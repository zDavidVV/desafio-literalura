package Main;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Author;
import models.Book;
import models.BookData;
import models.Data;
import repository.AuthorRepository;
import repository.BooksRepository;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    private Scanner scanner = new Scanner(System.in);
    private APIConsumer apiConsumer = new APIConsumer();
    private DataConverter dataConverter = new DataConverter();
    private BooksRepository bookRepository;
    private AuthorRepository authorRepository;
    private List<Book> books;

    private final String URL = "https://gutendex.com/books/";

    public Main(BooksRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }
    public void showMenu() {
        int option = -1;
        while (option != 0) {
            var menu = """
                    1 - Buscar libro 
                    2 - Mostrar libros buscados
                    3 - Búsqueda por idioma
                    4 - Mostrar Autores de libros buscados
                    5 - Autores vivos en un determinado año
                    6 - Cantidad de Libros por idioma
                                  
                    0 - Salir
                    """;
            System.out.println(menu);

            Scanner input = new Scanner(System.in);
            option = input.nextInt();

            switch (option) {
                case 1:
                    getBook();
                    break;
                case 2:
                    showBooks();
                    break;
                case 3:
                    searchByLanguage();
                    break;
                case 4:
                    showAuthors();
                    break;
                case 5:
                    authorsAliveInYear();
                    break;
                case 6:
                    countBooksByLanguage();
                    break;
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }
    }
    private Book getBook() {
        System.out.println("Escribe el nombre del libro que deseas buscar");
        Scanner name = new Scanner(System.in);
        String bookName = name.toString();

        var json = apiConsumer.getData(URL + "?search=" + bookName.replace(" ", "+"));
        var searchResult = dataConverter.getData(json, Data.class);
        Optional<BookData> searchedBook = searchResult.result().stream()
                .filter(b -> b.title().toUpperCase().contains(bookName.toUpperCase()))
                .findFirst();

        if (searchedBook.isPresent()) {
            System.out.println("¡Se encontró el libro!");
            var bookData = searchedBook.get();
            Book book = new Book(bookData);

            Author author = new Author(bookData.author().get(0));
            Optional<Author> existingAuthor = authorRepository.findByName(author.getName());
            if (existingAuthor.isPresent()) {
                book.setAuthor(existingAuthor.get());
            } else {
                authorRepository.save(author);
                book.setAuthor(author);
            }
            Optional<Book> existingBook = bookRepository.findByTitleContainsIgnoreCase(book.getTitle());
            if (existingBook.isPresent()) {
                System.out.println(existingBook.get());
            } else {
                bookRepository.save(book);
                System.out.println(book);
                return book;
            }

        } else {
            System.out.println("Libro no encontrado :(");
        }

        return null;
    }

    private void showBooks() {
        books = bookRepository.findAll();
        books.forEach(System.out::println);
    }

    private void searchByLanguage() {
        String selectedLanguage = null;
        System.out.println("Eliga el idioma: ");
        var menu = """
                    1 - Español
                    2 - Inglés
                    """;
        System.out.println(menu);
        Scanner input = new Scanner(System.in);
        int language = input.nextInt();;
        switch (language) {
            case 1:
                selectedLanguage = "es";
                break;
            case 2:
                selectedLanguage = "en";
                break;
            default:
                System.out.println("Opción inválida");
        }
        List<Book> booksByLanguage = bookRepository.findByLanguage(selectedLanguage);
        System.out.println("Libros en el idioma seleccionado:");
        booksByLanguage.forEach(b -> System.out.println("Título: " + b.getTitle() + ", Autor: " + b.getAuthor().getName() + " Descargas: " + b.getDownloadCount()));
    }

    private void showAuthors() {
        var authors = authorRepository.findAll();
        authors.forEach(System.out::println);
    }

    public void authorsAliveInYear() {
        System.out.println("Especifique el año: ");
        Scanner input = new Scanner(System.in);
        int year = input.nextInt();
        var result = authorRepository.findAuthorsAliveInYear(year);
        if (result.isEmpty()) {
            System.out.println("No se encontraron resultados");
        } else {
            System.out.println("Autores vivos en el año " + year + ":");
            List<Author> authorsOfTheYear = result;
            authorsOfTheYear.forEach(System.out::println);
        }
    }
    public void countBooksByLanguage() {
        String selectedLanguage = null;
        System.out.println("Eliga el idioma: ");
        var menu = """
                    1 - Español
                    2 - Inglés
                    """;
        System.out.println(menu);
        Scanner input = new Scanner(System.in);
        int language = input.nextInt();
        switch (language) {
            case 1:
                selectedLanguage = "es";
                break;
            case 2:
                selectedLanguage = "en";
                break;
            default:
                System.out.println("Opción inválida");
        }
        System.out.println("La cantidad de libros en el idioma seleccionado es: " + bookRepository.findByLanguage(selectedLanguage).size());
    }
    public class APIConsumer {
        public String getData(String url) {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();
            HttpResponse<String> response = null;
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return response.body();
        }
    }

    public class DataConverter implements IDataConverter {
        private ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public <T> T getData(String json, Class<T> clazz) {
            try {
                return objectMapper.readValue(json, clazz);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public interface IDataConverter {
        <T> T getData(String json, Class<T> clazz);
    }
}