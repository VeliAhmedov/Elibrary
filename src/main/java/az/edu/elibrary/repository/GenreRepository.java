package az.edu.elibrary.repository;

import az.edu.elibrary.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {
    List<Genre> findAllByActive(Integer active);
    Genre findGenreByIdAndActive(Long id, Integer active);
}
