package az.edu.elibrary.repository;

import az.edu.elibrary.entity.Customer;
import az.edu.elibrary.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {
    Rental findByTransactionIdAndActive(Long transactionId, Integer active);
    Rental findByIdAndActive(Long id, Integer active);
    List<Rental> findRentalByCustomerAndActive(Customer customer, Integer active);

}
