package az.edu.elibrary.service.impl;

import az.edu.elibrary.dto.request.ReqTransaction;
import az.edu.elibrary.dto.response.RespTransaction;
import az.edu.elibrary.dto.response.RespStatus;
import az.edu.elibrary.dto.response.Response;
import az.edu.elibrary.entity.*;
import az.edu.elibrary.repository.*;
import az.project.elibrary.entity.*;
import az.edu.elibrary.enums.EnumAvailableStatus;
import az.edu.elibrary.enums.EnumPaymentStatus;
import az.edu.elibrary.enums.EnumRentalStatus;
import az.edu.elibrary.enums.EnumTransactionStatus;
import az.edu.elibrary.exception.ExceptionConstants;
import az.edu.elibrary.exception.LibraryException;
import az.project.elibrary.repository.*;
import az.edu.elibrary.service.TransactionService;
import az.edu.elibrary.utils.Utility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;
    private final BasketRepository basketRepository;
    private final BookRepository bookRepository;
    private final RentalRepository rentalRepository;
    private final Utility utility;
    @Override
    public Response<RespTransaction> createTransaction(ReqTransaction reqTransaction, String token) {
        Response<RespTransaction> response = new Response<>();
        try {
            utility.checkToken(token);
            // checks null of customerId and basketId
            if (reqTransaction.getCustomerId() == null || reqTransaction.getBasketId() == null) {
                throw new LibraryException(ExceptionConstants.INVALID_REQUEST_DATA, "invalid request Data");
            }
            // check null as whole
            Customer customer = customerRepository.findById(reqTransaction.getCustomerId()).orElse(null);
            if (customer == null) {
                throw new LibraryException(ExceptionConstants.CUSTOMER_NOT_FOUND, "Customer not found");
            }
            Basket basket = basketRepository.findById(reqTransaction.getBasketId()).orElse(null);
            if (basket == null) {
                throw new LibraryException(ExceptionConstants.BASKET_NOT_FOUND, "Basket not found");
            }
            // check customerId of transaction is same as customerId of basket
            if (basket.getCustomer().getId() != reqTransaction.getCustomerId()) {
                throw new LibraryException(ExceptionConstants.BASKET_NOT_BELONG_TO_CUSTOMER, "Basket does not belong to the customer");
            }
            // checks if customer's customer balance has enough to pay
            if (customer.getBalance() < basket.getTotalPrice()) {
                throw new LibraryException(ExceptionConstants.INSUFFICIENT_BALANCE, "Insufficient balance");
            }
            // deduct price from balance
            customer.setBalance(customer.getBalance() - basket.getTotalPrice());
            customerRepository.save(customer);
            // builds
            Transaction transaction = Transaction.builder()
                    .customer(customer)
                    .basket(basket)
                    .transactionStatus(EnumTransactionStatus.FINISHED.getValue())
                    .build();
            transaction = transactionRepository.save(transaction);
            // after creating, basket will be labeled as PAID from NOT_PAID
            basket.setPaymentStatus(EnumPaymentStatus.PAID.name());
            basketRepository.save(basket);

            // automatically create rental data row, used calendar here, fist used LOCALDATE, but it didn't work for some reason
            Date rentalDate = transaction.getTransactionDate(); //will work on rental date
            Calendar calendar = Calendar.getInstance(); // getting calendar object for calculation
            calendar.setTime(rentalDate); //will calculate on rental date
            calendar.add(Calendar.MONTH, 1); // due date is always after 1 month so add 1 month
            Date dueDate = calendar.getTime(); //getting and making to duetime
            Rental rental = Rental.builder()
                    .transaction(transaction)
                    .customer(customer)
                    .book(basket.getBook())
                    .rentalDate(rentalDate)
                    .dueDate(dueDate) //setting calculated due time
                    .rentalStatus(EnumRentalStatus.ONGOING.name())
                    .build();
            rentalRepository.save(rental);
            // update book stock and held
            Book book = basket.getBook();
            book.setHeldBooks(book.getHeldBooks() - basket.getAmount()); // renting decreases held
            book.setStock(book.getStock() - basket.getAmount()); // renting decreases from stock
            book.setRentedBooks(book.getRentedBooks() + basket.getAmount()); // renting increases rentedBooks
            bookRepository.save(book);
            // Response
            RespTransaction respTransaction = convertToRespTransaction(transaction);
            response.setT(respTransaction);
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
    public Response<RespTransaction> getTransactionById(Long id,String token) {
        Response<RespTransaction> response = new Response<>();
        try {
            utility.checkToken(token);
            if (id == null) {
                throw new LibraryException(ExceptionConstants.INVALID_REQUEST_DATA, "invalid request Data");
            }

            Transaction transaction = transactionRepository.findById(id).orElse(null);
            if (transaction == null) {
                throw new LibraryException(ExceptionConstants.TRANSACTION_NOT_FOUND, "Transaction not found");
            }

            RespTransaction respTransaction = convertToRespTransaction(transaction);
            response.setT(respTransaction);
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
    public Response<List<RespTransaction>> getTransactionsByCustomerId(Long customerId,String token) {
        Response<List<RespTransaction>> response = new Response<>();
        try {
            utility.checkToken(token);
            if (customerId == null) {
                throw new LibraryException(ExceptionConstants.INVALID_REQUEST_DATA, "invalid request Data");
            }

            List<Transaction> transactionList = transactionRepository.findByCustomerIdAndActive(customerId, EnumAvailableStatus.ACTIVE.value);
            if (transactionList.isEmpty()) {
                throw new LibraryException(ExceptionConstants.TRANSACTION_NOT_FOUND, "Transactions not found");
            }

            List<RespTransaction> respTransactionList = transactionList.stream()
                    .map(this::convertToRespTransaction)
                    .collect(Collectors.toList());
            response.setT(respTransactionList);
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
    public Response<List<RespTransaction>> getAllTransactions(String token) {
        Response<List<RespTransaction>> response = new Response<>();
        try {
            utility.checkToken(token);
            List<Transaction> transactionList = transactionRepository.findAll();
            if (transactionList.isEmpty()) {
                throw new LibraryException(ExceptionConstants.TRANSACTION_NOT_FOUND, "Transactions not found");
            }

            List<RespTransaction> respTransactionList = transactionList.stream()
                    .map(this::convertToRespTransaction)
                    .collect(Collectors.toList());
            response.setT(respTransactionList);
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
    public Response<RespTransaction> refundTransaction(Long transactionId,String token) {
        Response<RespTransaction> response = new Response<>();
        try {
            utility.checkToken(token);
            //Transaction transaction = transactionRepository.findById(transactionId).orElse(null);
            Transaction transaction = transactionRepository.findByIdAndActive(transactionId, EnumAvailableStatus.ACTIVE.value);
            if (transaction == null) {
                throw new LibraryException(ExceptionConstants.TRANSACTION_NOT_FOUND, "Transaction not found");
            }
            //TODO: fix unlimited refund problem / fixed

            //this here checks if transaction is already refunded
            if (EnumTransactionStatus.REFUNDED.getValue().equals(transaction.getTransactionStatus())) {
                throw new LibraryException(ExceptionConstants.TRANSACTION_ALREADY_REFUNDED, "Transaction is already refunded");
            }
            //getting current time,
            Date currentDate = new Date();
            // turn millisecond for some reason for better accuracy
            long differenceMilli = Math.abs(currentDate.getTime() - transaction.getTransactionDate().getTime());
            // teh turn that into days
            long differenceDay = TimeUnit.DAYS.convert(differenceMilli, TimeUnit.MILLISECONDS);
            //2 days limit to refund, after that you can't refund
            if (differenceDay > 2) {
                throw new LibraryException(ExceptionConstants.REFUND_EXPIRATION, "Refund period has expired");
            }
            Customer customer = transaction.getCustomer();
            Basket basket = transaction.getBasket();
            // return paid price to balance
            customer.setBalance(customer.getBalance() + basket.getTotalPrice());
            customerRepository.save(customer);
            // change label to refund
            transaction.setTransactionStatus(EnumTransactionStatus.REFUNDED.getValue());
            transactionRepository.save(transaction);
            // did add new value to EnumPaymentStatus
            basket.setPaymentStatus(EnumPaymentStatus.REFUNDED.name());
            basketRepository.save(basket);
            // Update book stock and available books
            Book book = basket.getBook();
            book.setStock(book.getStock() + basket.getAmount()); // refund return stock
            book.setRentedBooks(book.getRentedBooks() - basket.getAmount()); // refund decreases rentedBooks
            bookRepository.save(book);
            // refunding transaction means we cancel rent process
            Rental rental = rentalRepository.findByTransactionIdAndActive(transactionId, EnumAvailableStatus.ACTIVE.value);
            if (rental != null) {
                rental.setRentalStatus(EnumRentalStatus.CANCELED.name());
                rentalRepository.save(rental);
            }
            RespTransaction respTransaction = convertToRespTransaction(transaction);
            response.setT(respTransaction);
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
    public Response<RespTransaction> getTransactionByBasketId(Long basketId, String token) {
        Response<RespTransaction> response = new Response<>();
        try {
            utility.checkToken(token);
            if (basketId == null) {
                throw new LibraryException(ExceptionConstants.INVALID_REQUEST_DATA, "INVALID REQUEST DATA");
            }
            Transaction transaction = transactionRepository.findTransactionByBasketIdAndActive(basketId, EnumAvailableStatus.ACTIVE.value);
            if (transaction == null) {
                throw new LibraryException(ExceptionConstants.TRANSACTION_NOT_FOUND, "Transaction not found");
            }
            RespTransaction respTransaction = convertToRespTransaction(transaction);
            response.setT(respTransaction);
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

    private RespTransaction convertToRespTransaction(Transaction transaction) {
        String formattedTransactionAmount = String.format("%.2f", transaction.getBasket().getTotalPrice());
        Double formattedPrice = Double.valueOf(formattedTransactionAmount);
        return RespTransaction.builder()
                .id(transaction.getId())
                .userName(transaction.getCustomer().getUser().getUsername())
                .bookTitle(transaction.getBasket().getBook().getTitle())
                .quantity(transaction.getBasket().getAmount())
                .transactionAmount(formattedPrice)
                .transactionDate(transaction.getTransactionDate())
                .transactionStatus(transaction.getTransactionStatus())
                .build();
    }
}
