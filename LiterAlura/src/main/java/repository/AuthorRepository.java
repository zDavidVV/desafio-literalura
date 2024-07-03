package repository;
import models.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface AuthorRepository extends JpaRepository<Author, Long>{
    Optional<Author> findByName(String name);

    @Query("SELECT A FROM Author AS A WHERE A.birthDate <= :year AND A.deathDate >= :year")
    List<Author> findAuthorsAliveInYear(int year);
}
