package models;
import jakarta.persistence.*;

import java.util.Collections;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;
@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true)
    private String title;
    @ManyToOne
    private Author author;
    private String language;
    private Integer downloadCount;

    public Book() {
    }

    public Book(BookData bookData) {
        this.title = bookData.title();
        this.language = bookData.languages().get(0);
        this.downloadCount = bookData.downloadCount();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Integer getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(Integer downloadCount) {
        this.downloadCount = downloadCount;
    }

    @Override
    public String toString() {
        return "Book=" + title +
                ", Author='" + author.getName() + '\'' +
                ", Language=" + language +
                ", Downloads=" + downloadCount;
    }
}
