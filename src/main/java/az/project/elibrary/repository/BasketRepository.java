package az.project.elibrary.repository;

import az.project.elibrary.entity.Basket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BasketRepository extends JpaRepository<Basket, Long> {

    List<Basket> findAllByActive(Integer active);

    Basket findBasketByIdAndActive(Long id, Integer active);

    List<Basket> findBasketByCustomerIdAndActive(Long customerId, Integer active);

//    Optional<Basket> findByAccountIdAndBookIdAndActive(Long accountId, Long bookId, Integer active);

    List<Basket> findBasketByCustomerIdAndBookIdAndActive(Long customerId, Long bookId, Integer active);
    List<Basket> findBasketByBookIdAndActive(Long bookId, Integer active);

}
