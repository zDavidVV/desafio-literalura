package repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import models.Book;
import org.springframework.stereotype.Repository;

@Repository
public interface BooksRepository extends JpaRepository<Book, Long>{
    Optional<Book> findByTitleContainsIgnoreCase(String title);

    List<Book> findByLanguage(String language);
}
