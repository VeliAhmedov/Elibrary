package az.edu.elibrary.repository;

import az.edu.elibrary.entity.Customer;
import az.edu.elibrary.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findAllByActive(Integer active);

    Customer findCustomerByIdAndActive(Long id, Integer active);

    Customer findCustomerByLibraryCardNumberAndActive(String libraryCardNumber, Integer active);

    Customer findCustomerByUserAndActive(User user, Integer active);
}
