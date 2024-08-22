package az.project.springbootproject.repository;

import az.project.springbootproject.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {
    List<Genre> findAllByActive(Integer active);
    Genre findGenreByIdAndActive(Long id, Integer active);
}
