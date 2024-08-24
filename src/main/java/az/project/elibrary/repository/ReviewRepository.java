package az.project.elibrary.repository;

import az.project.elibrary.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findAllByActive(Integer active);

    Review findByIdAndActive(Long id, Integer active);

    List<Review> findByCustomerIdAndActive(Long customerId, Integer active);

    List<Review> findByBookIdAndActive(Long bookId, Integer active);

}
