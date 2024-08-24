package az.project.elibrary.service.impl;

import az.project.elibrary.dto.response.RespRental;
import az.project.elibrary.dto.response.RespStatus;
import az.project.elibrary.dto.response.Response;
import az.project.elibrary.entity.*;
import az.project.elibrary.enums.EnumAvailableStatus;
import az.project.elibrary.enums.EnumRentalStatus;
import az.project.elibrary.exception.ExceptionConstants;
import az.project.elibrary.exception.LibraryException;
import az.project.elibrary.repository.*;
import az.project.elibrary.service.BookService;
import az.project.elibrary.service.RentalService;
import az.project.elibrary.utils.Utility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {

    private final RentalRepository rentalRepository;
    private final CustomerRepository customerRepository;
    private final BookService bookService;
    private final BookRepository bookRepository;
    private final BasketRepository basketRepository;
    private final Utility utility;


    @Override
    public Response<RespRental> getRentalById(Long id,String token) {
        Response<RespRental> response = new Response<>();
        try {
            utility.checkToken(token);
            Rental rental = rentalRepository.findByIdAndActive(id, EnumAvailableStatus.ACTIVE.value);
            if (rental == null) {
                throw new LibraryException(ExceptionConstants.RENTAL_NOT_FOUND, "Rental not found");
            }
            RespRental respRental = convertToRespRental(rental);
            response.setT(respRental);
            response.setStatus(RespStatus.getSuccessMessage());
        } catch (LibraryException ex) {
            ex.printStackTrace();
            response.setStatus(new RespStatus(ex.getCode(), ex.getMessage()));
        } catch (Exception ex) {
            ex.printStackTrace();
            response.setStatus(new RespStatus(ExceptionConstants.INTERNAL_EXCEPTION, "Internal Exception"));
        }
//        updateRentalStatuses(); // continuous updating
        return response;
    }

    @Override
    public Response<RespRental> returnBook(Long rentalId,String token) {
        Response<RespRental> response = new Response<>();
        try {
            utility.checkToken(token);
            // checking rental null
            Rental rental = rentalRepository.findByIdAndActive(rentalId, EnumAvailableStatus.ACTIVE.value);
            if (rental == null) {
                throw new LibraryException(ExceptionConstants.RENTAL_NOT_FOUND, "Rental not found");
            }
            // Checking if the rental is already returned or canceled
            if (EnumRentalStatus.CANCELED.name().equals(rental.getRentalStatus())) {
                throw new LibraryException(ExceptionConstants.RENTAL_ALREADY_CANCELED, "Rental already canceled");
            }
            if (EnumRentalStatus.RETURNED.name().equals(rental.getRentalStatus()) || EnumRentalStatus.OVERDUE_RETURNED.name().equals(rental.getRentalStatus())) {
                throw new LibraryException(ExceptionConstants.RENTAL_ALREADY_RETURNED, "Rental already returned");
            }
            // given condition find LateFee or if there is fee
            Double lateFee = 0.0;
            Date currentDate = new Date();
            if (rental.getDueDate().before(currentDate) && rental.getReturnDate() == null) { //make sure book hasn't been returned
                long differenceInMillis = currentDate.getTime() - rental.getDueDate().getTime();
                long differenceInDays = (differenceInMillis + (24 * 60 * 60 * 1000) - 1) / (24 * 60 * 60 * 1000); //mill to day,
                lateFee = differenceInDays * 0.5; //even 1 min pass from 24 hours, it counts as day
            }

            // if fee exists make it overdue_return, if not return
            rental.setRentalStatus(lateFee > 0 ? EnumRentalStatus.OVERDUE_RETURNED.name() : EnumRentalStatus.RETURNED.name());
            rental.setReturnDate(currentDate);
            rental.setLateFee(lateFee);
            rentalRepository.save(rental);

            // for amount, getting transaction of rental
            Transaction transaction = rental.getTransaction();
            if (transaction == null) {
                throw new LibraryException(ExceptionConstants.TRANSACTION_NOT_FOUND, "Transaction not found");
            }
            // getting basket of rental
            Basket basket = basketRepository.findById(transaction.getBasket().getId())
                    .orElseThrow(() -> new LibraryException(ExceptionConstants.BASKET_NOT_FOUND, "Basket not found"));

            // update stock and available
            Book book = rental.getBook();
            int amount = basket.getAmount();
            book.setStock(book.getStock() + amount);
            book.setRentedBooks(book.getRentedBooks()-amount);
            bookRepository.save(book);

            RespRental respRental = convertToRespRental(rental);
            response.setT(respRental);
            response.setStatus(RespStatus.getSuccessMessage());

        } catch (LibraryException ex) {
            ex.printStackTrace();
            response.setStatus(new RespStatus(ex.getCode(), ex.getMessage()));
        } catch (Exception ex) {
            ex.printStackTrace();
            response.setStatus(new RespStatus(ExceptionConstants.INTERNAL_EXCEPTION, "Internal Exception"));
        }
        return response;
    }


    @Override
    public Response<List<RespRental>> listRentals(String token) {
        Response<List<RespRental>> response = new Response<>();
        try {
            utility.checkToken(token);
            List<Rental> rentals = rentalRepository.findAll();
            List<RespRental> respRentals = rentals.stream()
                    .map(this::convertToRespRental)
                    .collect(Collectors.toList());
            response.setT(respRentals);
            response.setStatus(RespStatus.getSuccessMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            response.setStatus(new RespStatus(ExceptionConstants.INTERNAL_EXCEPTION, "Internal Exception"));
        }
//        updateRentalStatuses(); //continuous updating
        return response;
    }
    @Override
    public Response<List<RespRental>> getRentalsByCustomerId(Long customerId, String token) {
        Response<List<RespRental>> response = new Response<>();
        try {
            utility.checkToken(token);

            // Fetching the customer by ID
            Customer customer = customerRepository.findCustomerByIdAndActive(customerId, EnumAvailableStatus.ACTIVE.value);
            if (customer == null) {
                throw new LibraryException(ExceptionConstants.CUSTOMER_NOT_FOUND, "Customer not found");
            }

            // Fetching rentals by customer ID
            List<Rental> rentals = rentalRepository.findRentalByCustomerAndActive(customer, EnumAvailableStatus.ACTIVE.value);
            List<RespRental> respRentals = rentals.stream()
                    .map(this::convertToRespRental)
                    .collect(Collectors.toList());

            response.setT(respRentals);
            response.setStatus(RespStatus.getSuccessMessage());
        } catch (LibraryException ex) {
            ex.printStackTrace();
            response.setStatus(new RespStatus(ex.getCode(), ex.getMessage()));
        } catch (Exception ex) {
            ex.printStackTrace();
            response.setStatus(new RespStatus(ExceptionConstants.INTERNAL_EXCEPTION, "Internal Exception"));
        }
        return response;
    }

    private RespRental convertToRespRental(Rental rental) {
        return RespRental.builder()
                .id(rental.getId())
                .userName(rental.getCustomer().getUser().getUsername() != null ? rental.getCustomer().getUser().getUsername() : null)
                .bookTitle(rental.getBook() != null ? rental.getBook().getTitle() : null)
                .rentalDate(rental.getRentalDate())
                .dueDate(rental.getDueDate())
                .returnDate(rental.getReturnDate())
                .rentalStatus(rental.getRentalStatus())
                .lateFee(rental.getLateFee())
                .build();
    }
    //THIS CODE HERE SUPPOSED TO CHECK IF CUSTOMER'S DUE TIME PASSED IF PASSED, IT WILL AUTOMATICALLY
    // MAKE RENTAL_STATUS TO OVERDUE FROM ONGOING
    //BUT IT IS LOOP, SO IT WILL AFFECT PERFORMANCE SO I DON'T KNOW HOW RIGHT THIS IS
//    private void updateRentalStatuses() {
//        Date currentDate = new Date();
//        List<Rental> rentals = rentalRepository.findAll();
//        for (Rental rental : rentals) {
//            if (rental.getDueDate().before(currentDate) && rental.getRentalStatus().equals("ONGOING") && rental.getReturnDate() == null) {
//                rental.setRentalStatus("OVERDUE");
//                rentalRepository.save(rental);
//            }
//        }
//    }

}
