package az.edu.elibrary.repository;

import az.edu.elibrary.entity.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {
    List<Discount> findAllByActive(Integer active);
    Discount findDiscountByIdAndActive(Long id, Integer active);

    List<Discount> findByStartDateBeforeAndEndDateAfter(Date startDate, Date endDate);

    //this would have help with find existence of interval problem
    Boolean existsByActiveAndStartDateLessThanEqualAndEndDateGreaterThanEqual(Integer Active, Date StartDate, Date EndDate);
    //SCRAPPED
    List<Discount> findByActiveAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Integer active, Date startDate, Date endDate
    );
}
