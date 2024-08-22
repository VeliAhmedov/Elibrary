package az.project.springbootproject.service.impl;

import az.project.springbootproject.dto.request.ReqBasket;
import az.project.springbootproject.dto.response.RespBasket;
import az.project.springbootproject.dto.response.RespStatus;
import az.project.springbootproject.dto.response.Response;
import az.project.springbootproject.entity.Basket;
import az.project.springbootproject.entity.Book;
import az.project.springbootproject.entity.Customer;
import az.project.springbootproject.enums.EnumAvailableStatus;
import az.project.springbootproject.enums.EnumPaymentStatus;
import az.project.springbootproject.enums.EnumProgressStatus;
import az.project.springbootproject.exception.ExceptionConstants;
import az.project.springbootproject.exception.LibraryException;
import az.project.springbootproject.repository.BasketRepository;
import az.project.springbootproject.repository.BookRepository;
import az.project.springbootproject.repository.CustomerRepository;
import az.project.springbootproject.service.BasketService;
import az.project.springbootproject.utils.Utility;
import lombok.Locked;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasketServiceImpl implements BasketService {
    private final BasketRepository basketRepository;
    private final BookRepository bookRepository;
    private final CustomerRepository customerRepository;
    private final Utility utility;
    private static final Logger LOGGER = LoggerFactory.getLogger(PublisherServiceImpl.class);

    @Override
    public Response<List<RespBasket>> getBasketList(String token) {
        Response<List<RespBasket>> response = new Response<>();
        try {
            LOGGER.info("getCustomerList request has been done with token: " + token);
            utility.checkToken(token);
            List<Basket> basketList = basketRepository.findAllByActive(EnumAvailableStatus.ACTIVE.value);
            if (basketList.isEmpty()) {
                LOGGER.warn("there is no active basket in CustomerList ");
                throw new LibraryException(ExceptionConstants.BASKET_NOT_FOUND, "Baskets not found");
            }
            List<RespBasket> respBasketList = basketList.stream()
                    //filter will filter ones that is removed ( it isn't same as active, so I didn't put on repository)
                    .filter(basket -> !EnumProgressStatus.REMOVED.getValue().equals(basket.getProgress()))
                    .map(this::convertToRespBasket)
                    .collect(Collectors.toList());
            response.setT(respBasketList);
            LOGGER.info("getBasketList successfully returned response with baskets: " + respBasketList);
            response.setStatus(RespStatus.getSuccessMessage());
        } catch (LibraryException ex) {
            ex.printStackTrace();
            LOGGER.error("LibraryException occurred in getBasketList: ", ex);
            response.setStatus(new RespStatus(ex.getCode(), ex.getMessage()));
        } catch (Exception ex) {
            ex.printStackTrace();
            LOGGER.error("Exception occurred in getBasketList: ", ex);
            response.setStatus(new RespStatus(ExceptionConstants.INTERNAL_EXCEPTION, "Internal Exception"));
        }
        return response;
    }


    @Override
    public Response<RespBasket> getBasketById(Long id, String token) {
        Response<RespBasket> response = new Response<>();
        try {
            LOGGER.info("getBasketById request initiated with id: " + id + " and token: " + token);
            utility.checkToken(token);
            if (id == null) {
                LOGGER.warn("Invalid request data: id is null.");
                throw new LibraryException(ExceptionConstants.BASKET_NOT_FOUND, "Basket not found");
            }
            Basket basket = basketRepository.findBasketByIdAndActive(id, EnumAvailableStatus.ACTIVE.value);
            if (basket == null) {
                LOGGER.warn("Basket with id: " + id + " not found.");
                throw new LibraryException(ExceptionConstants.BASKET_NOT_FOUND, "Basket not found");
            }
            RespBasket respBasket = convertToRespBasket(basket);
            response.setT(respBasket);
            LOGGER.info("getBasketById successfully returned response with basket: " + respBasket);
            response.setStatus(RespStatus.getSuccessMessage());
        } catch (LibraryException ex) {
            ex.printStackTrace();
            LOGGER.error("LibraryException occurred in getBasketById: ", ex);
            response.setStatus(new RespStatus(ex.getCode(), ex.getMessage()));
        } catch (Exception ex) {
            ex.printStackTrace();
            LOGGER.error("Exception occurred in getBasketById: ", ex);
            response.setStatus(new RespStatus(ExceptionConstants.INTERNAL_EXCEPTION, "Internal Exception"));
        }
        return response;
    }


    @Override
    public Response<RespBasket> createBasket(ReqBasket reqBasket, String token) {
        Response<RespBasket> response = new Response<>();
        try {
            LOGGER.info("CreateBasket request initiated with token: " + token);
            utility.checkToken(token);
            if (reqBasket.getCustomerId() == null || reqBasket.getBookId() == null) {
                LOGGER.warn("Invalid request data: customerId or bookId is null.");
                throw new LibraryException(ExceptionConstants.INVALID_REQUEST_DATA, "invalid request data");
            }
            Customer customer = customerRepository.findCustomerByIdAndActive(reqBasket.getCustomerId(), EnumAvailableStatus.ACTIVE.value);
            if (customer == null) {
                LOGGER.warn("Customer with id: " + reqBasket.getCustomerId() + " not found.");
                throw new LibraryException(ExceptionConstants.CUSTOMER_NOT_FOUND, "customer not found");
            }
            Book book = bookRepository.findBookByIdAndActive(reqBasket.getBookId(), EnumAvailableStatus.ACTIVE.value);
            if (book == null) {
                LOGGER.warn("Book with id: " + reqBasket.getBookId() + " not found.");
                throw new LibraryException(ExceptionConstants.BOOK_NOT_FOUND, "Book not found");
            }
            // you can only rent max 14 books
            if (reqBasket.getAmount() > 15) {
                LOGGER.warn("Cannot rent more than 15 books at a time.");
                throw new LibraryException(ExceptionConstants.BASKET_RENT_AMOUNT_LIMIT, "Cannot rent more than 8 books at a time");
            }
            // Check if the requested amount is available
            if (book.getStock() < reqBasket.getAmount()) {
                LOGGER.warn("Not enough books available: requested amount is " + reqBasket.getAmount() + ", available stock is " + book.getStock());
                throw new LibraryException(ExceptionConstants.BOOK_STOCK_SHORTAGE, "Not enough books available");
            }
            Basket basket = Basket.builder()
                    .customer(customer)
                    .book(book)
                    .amount(reqBasket.getAmount())
                    .totalPrice(book.getPrice() * reqBasket.getAmount())
                    .build();

            basket = basketRepository.save(basket);

            // update  held
            book.setHeldBooks(book.getHeldBooks() + reqBasket.getAmount());
            bookRepository.save(book);

            RespBasket respBasket = convertToRespBasket(basket);
            response.setT(respBasket);
            LOGGER.info("Basket created successfully: " + respBasket);
            response.setStatus(RespStatus.getSuccessMessage());
        } catch (LibraryException ex) {
            ex.printStackTrace();
            LOGGER.error("LibraryException occurred in createBasket: ", ex);
            response.setStatus(new RespStatus(ex.getCode(), ex.getMessage()));
        } catch (Exception ex) {
            ex.printStackTrace();
            LOGGER.error("Exception occurred in createBasket: ", ex);
            response.setStatus(new RespStatus(ExceptionConstants.INTERNAL_EXCEPTION, "Internal Exception"));
        }
        return response;
    }

    @Override
    public Response<List<RespBasket>> getBasketsByCustomerId(Long customerId, String token) {
        Response<List<RespBasket>> response = new Response<>();
        try {
            LOGGER.info("getBasketsByCustomerId request initiated with customerId: " + customerId + " and token: " + token);
            utility.checkToken(token);
            if (customerId == null) {
                LOGGER.warn("Invalid request data: customerId is null.");
                throw new LibraryException(ExceptionConstants.INVALID_REQUEST_DATA, "invalid request data");
            }
            List<Basket> basketList = basketRepository.findBasketByCustomerIdAndActive(customerId, EnumAvailableStatus.ACTIVE.value);
            if (basketList.isEmpty()) {
                LOGGER.warn("No baskets found for customerId: " + customerId);
                throw new LibraryException(ExceptionConstants.BASKET_NOT_FOUND, "Baskets not found");
            }
            List<RespBasket> respBasketList = basketList.stream()
                    .map(this::convertToRespBasket)
                    .collect(Collectors.toList());
            response.setT(respBasketList);
            LOGGER.info("getBasketsByCustomerId successfully returned response with baskets: " + respBasketList);
            response.setStatus(RespStatus.getSuccessMessage());
        } catch (LibraryException ex) {
            ex.printStackTrace();
            LOGGER.error("LibraryException occurred in getBasketsByCustomerId: ", ex);
            response.setStatus(new RespStatus(ex.getCode(), ex.getMessage()));
        } catch (Exception ex) {
            ex.printStackTrace();
            LOGGER.error("Exception occurred in getBasketsByCustomerId: ", ex);
            response.setStatus(new RespStatus(ExceptionConstants.INTERNAL_EXCEPTION, "Internal Exception"));
        }
        return response;
    }
    @Override
    public Response<List<RespBasket>> getBasketsByBookId(Long bookId, String token) {
        Response<List<RespBasket>> response = new Response<>();
        try {
            LOGGER.info("getBasketsByBookId request initiated with bookId: " + bookId + " and token: " + token);
            utility.checkToken(token);
            if (bookId == null) {
                LOGGER.warn("Invalid request data: bookId is null.");
                throw new LibraryException(ExceptionConstants.INVALID_REQUEST_DATA, "invalid request data");
            }
            List<Basket> basketList = basketRepository.findBasketByBookIdAndActive(bookId, EnumAvailableStatus.ACTIVE.value);
            if (basketList.isEmpty()) {
                LOGGER.warn("No baskets found for bookId: " + bookId);
                throw new LibraryException(ExceptionConstants.BASKET_NOT_FOUND, "Baskets not found");
            }
            List<RespBasket> respBasketList = basketList.stream()
                    .map(this::convertToRespBasket)
                    .collect(Collectors.toList());
            response.setT(respBasketList);
            LOGGER.info("getBasketsByBookId successfully returned response with baskets: " + respBasketList);
            response.setStatus(RespStatus.getSuccessMessage());
        } catch (LibraryException ex) {
            ex.printStackTrace();
            LOGGER.error("LibraryException occurred in getBasketsByBookId: ", ex);
            response.setStatus(new RespStatus(ex.getCode(), ex.getMessage()));
        } catch (Exception ex) {
            ex.printStackTrace();
            LOGGER.error("Exception occurred in getBasketsByBookId: ", ex);
            response.setStatus(new RespStatus(ExceptionConstants.INTERNAL_EXCEPTION, "Internal Exception"));
        }
        return response;
    }


    @Override
    public Response<RespBasket> removeBasket(Long id, String token) {
        Response<RespBasket> response = new Response<>();
        try {
            LOGGER.info("removeBasket request initiated with id: " + id + " and token: " + token);
            utility.checkToken(token);
            if (id == null) {
                LOGGER.warn("Invalid request data: id is null.");
                throw new LibraryException(ExceptionConstants.INVALID_REQUEST_DATA, "invalid request data");
            }
            Basket basket = basketRepository.findBasketByIdAndActive(id, EnumAvailableStatus.ACTIVE.value);
            if (basket == null) {
                throw new LibraryException(ExceptionConstants.BASKET_NOT_FOUND, "Basket not found");
            }
            // checks if it is already removed, you can't remove removed
            if (EnumProgressStatus.REMOVED.getValue().equals(basket.getProgress())) {
                throw new LibraryException(ExceptionConstants.BASKET_ALREADY_REMOVED, "Basket is already removed");
            }
            // this one make sure  ones that is paymentStatus labeled as "PAID" can't be removed
            if (EnumPaymentStatus.PAID.name().equals(basket.getPaymentStatus())) {
                throw new LibraryException(ExceptionConstants.BASKET_ALREADY_PAID, "This basket already has been paid");
            }
            //this is one make sure that payment status refunded can't be removed
            if (EnumPaymentStatus.REFUNDED.name().equals(basket.getPaymentStatus())) {
                throw new LibraryException(ExceptionConstants.BASKET_ALREADY_REFUNDED, "This basket already has been refunded");
            }
            // update held and available
            Book book = basket.getBook();
            book.setHeldBooks(book.getHeldBooks() - basket.getAmount());
            bookRepository.save(book);
            // label progress as removed
            basket.setProgress(EnumProgressStatus.REMOVED.getValue());
            basketRepository.save(basket);
            response.setStatus(RespStatus.getSuccessMessage());
        } catch (LibraryException ex) {
            ex.printStackTrace();
            LOGGER.error("LibraryException occurred in removeBasket: ", ex);
            response.setStatus(new RespStatus(ex.getCode(), ex.getMessage()));
        } catch (Exception ex) {
            ex.printStackTrace();
            LOGGER.error("Exception occurred in removeBasket: ", ex);
            response.setStatus(new RespStatus(ExceptionConstants.INTERNAL_EXCEPTION, "Internal Exception"));
        }
        return response;
    }

    private RespBasket convertToRespBasket(Basket basket) {
        String formatTotalPriceWithString = String.format("%.2f", basket.getTotalPrice() );
        Double formattedPrice = Double.valueOf(formatTotalPriceWithString);
        return RespBasket.builder()
                .id(basket.getId())
                .bookTitle(basket.getBook().getTitle())
                .userName(basket.getCustomer().getUser().getUsername())
                .amount(basket.getAmount())
                .totalPrice(formattedPrice)
                .paymentStatus(basket.getPaymentStatus())
                .build();
    }
}
