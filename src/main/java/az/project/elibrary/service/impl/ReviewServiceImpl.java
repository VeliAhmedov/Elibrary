package az.project.elibrary.service.impl;

import az.project.elibrary.dto.request.ReqReview;
import az.project.elibrary.dto.response.RespReview;
import az.project.elibrary.dto.response.RespStatus;
import az.project.elibrary.dto.response.Response;
import az.project.elibrary.entity.Book;
import az.project.elibrary.entity.Basket;
import az.project.elibrary.entity.Customer;
import az.project.elibrary.entity.Review;
import az.project.elibrary.enums.EnumAvailableStatus;
import az.project.elibrary.enums.EnumPaymentStatus;
import az.project.elibrary.enums.EnumProgressStatus;
import az.project.elibrary.exception.ExceptionConstants;
import az.project.elibrary.exception.LibraryException;
import az.project.elibrary.repository.BookRepository;
import az.project.elibrary.repository.BasketRepository;
import az.project.elibrary.repository.CustomerRepository;
import az.project.elibrary.repository.ReviewRepository;
import az.project.elibrary.service.ReviewService;
import az.project.elibrary.utils.Utility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final CustomerRepository customerRepository;
    private final BookRepository bookRepository;
    private final BasketRepository basketRepository;
    private final Utility utility;

    @Override
    public Response<List<RespReview>> getAllReviews(String token) {
        Response<List<RespReview>> response = new Response<>();
        try {
            utility.checkToken(token);
            List<Review> reviewList = reviewRepository.findAllByActive(EnumAvailableStatus.ACTIVE.value);
            if (reviewList.isEmpty()) {
                throw new LibraryException(ExceptionConstants.REVIEW_NOT_FOUND, "Reviews not found");
            }
            List<RespReview> respReviewList = reviewList.stream()
                    .map(this::convertToRespReview)
                    .collect(Collectors.toList());
            response.setT(respReviewList);
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
    public Response<RespReview> getReviewById(Long id,String token) {
        Response<RespReview> response = new Response<>();
        try {
            utility.checkToken(token);
            if (id == null) {
                throw new LibraryException(ExceptionConstants.INVALID_REQUEST_DATA, "invalid request Data");
            }
            Review review = reviewRepository.findByIdAndActive(id, EnumAvailableStatus.ACTIVE.value);
            if (review == null) {
                throw new LibraryException(ExceptionConstants.REVIEW_NOT_FOUND, "Review not found");
            }
            RespReview respReview = convertToRespReview(review);
            response.setT(respReview);
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
    public Response<RespReview> createReview(ReqReview reqReview,String token) {
        Response<RespReview> response = new Response<>();
        try {
            utility.checkToken(token);
            if (reqReview.getCustomerId() == null || reqReview.getBookId() == null || reqReview.getReview() == null || reqReview.getRating() == null) {
                throw new LibraryException(ExceptionConstants.INVALID_REQUEST_DATA, "Invalid request data");
            }
            // rating can't be decimal and only 1 to 10
            if (reqReview.getRating() < 1 || reqReview.getRating() > 10) {
                throw new LibraryException(ExceptionConstants.REVIEW_RATING_LIMIT, "Rating must be between 1 and 10");
            }
            Customer customer = customerRepository.findById(reqReview.getCustomerId()).orElse(null);
            if (customer == null) {
                throw new LibraryException(ExceptionConstants.CUSTOMER_NOT_FOUND, "customer not found");
            }
            Book book = bookRepository.findById(reqReview.getBookId()).orElse(null);
            if (book == null) {
                throw new LibraryException(ExceptionConstants.BOOK_NOT_FOUND, "Book not found");
            }
            // check if customer of customer has rented book from that customer
            List<Basket> baskets = basketRepository.findBasketByCustomerIdAndBookIdAndActive(reqReview.getCustomerId(), reqReview.getBookId(), EnumAvailableStatus.ACTIVE.value);
            // for that it must be selected ( not removed or refunded) and must be paid ( not not_paid or refunded)
            boolean canReview = baskets.stream()
                    .anyMatch(basket ->
                            EnumProgressStatus.SELECTED.getValue().equalsIgnoreCase(basket.getProgress()) &&
                                    EnumPaymentStatus.PAID.name().equalsIgnoreCase(basket.getPaymentStatus())
                    );
            //checks here
            if (!canReview) {
                throw new LibraryException(ExceptionConstants.NOT_REVIEW_NOT_OWNED_BOOK, "The customer cannot review this book");
            }
            // creating review
            Review review = Review.builder()
                    .customer(customer)
                    .book(book)
                    .review(reqReview.getReview())
                    .rating(reqReview.getRating())
                    .build();
            review = reviewRepository.save(review);
            RespReview respReview = convertToRespReview(review);
            response.setT(respReview);
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
    public Response<RespReview> updateReview(Long id, ReqReview reqReview,String token) {
        Response<RespReview> response = new Response<>();
        try {
            utility.checkToken(token);
            if (id == null || reqReview.getReview() == null || reqReview.getRating() == null) {
                throw new LibraryException(ExceptionConstants.INVALID_REQUEST_DATA, "invalid request Data");
            }
            Review review = reviewRepository.findByIdAndActive(id, EnumAvailableStatus.ACTIVE.value);
            if (review == null) {
                throw new LibraryException(ExceptionConstants.REVIEW_NOT_FOUND, "Review not found");
            }
            if (reqReview.getRating() < 1 || reqReview.getRating() > 10) {
                throw new LibraryException(ExceptionConstants.REVIEW_RATING_LIMIT, "Rating must be between 1 and 10");
            }
            review.setReview(reqReview.getReview());
            review.setRating(reqReview.getRating());
            review = reviewRepository.save(review);

            RespReview respReview = convertToRespReview(review);
            response.setT(respReview);
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
    public Response<RespReview> deleteReview(Long id,String token) {
        Response<RespReview> response = new Response<>();
        try {
            utility.checkToken(token);
            if (id == null) {
                throw new LibraryException(ExceptionConstants.INVALID_REQUEST_DATA, "invalid request Data");
            }
            Review review = reviewRepository.findByIdAndActive(id, EnumAvailableStatus.ACTIVE.value);
            if (review == null) {
                throw new LibraryException(ExceptionConstants.REVIEW_NOT_FOUND, "Review not found");
            }
            review.setActive(EnumAvailableStatus.DEACTIVE.value);
            reviewRepository.save(review);
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
    public Response<List<RespReview>> getReviewsByCustomerId(Long customerId,String token) {
        Response<List<RespReview>> response = new Response<>();
        try {
            utility.checkToken(token);
            if (customerId == null) {
                throw new LibraryException(ExceptionConstants.INVALID_REQUEST_DATA, "invalid request Data");
            }
            List<Review> reviewList = reviewRepository.findByCustomerIdAndActive(customerId, EnumAvailableStatus.ACTIVE.value);
            if (reviewList.isEmpty()) {
                throw new LibraryException(ExceptionConstants.REVIEW_NOT_FOUND, "Reviews not found");
            }
            List<RespReview> respReviewList = reviewList.stream()
                    .map(this::convertToRespReview)
                    .collect(Collectors.toList());
            response.setT(respReviewList);
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
    public Response<List<RespReview>> getReviewsByBookId(Long bookId,String token) {
        Response<List<RespReview>> response = new Response<>();
        try {
            utility.checkToken(token);
            if (bookId == null) {
                throw new LibraryException(ExceptionConstants.INVALID_REQUEST_DATA, "Invalid request data");
            }
            List<Review> reviewList = reviewRepository.findByBookIdAndActive(bookId, EnumAvailableStatus.ACTIVE.value);
            if (reviewList.isEmpty()) {
                throw new LibraryException(ExceptionConstants.REVIEW_NOT_FOUND, "Reviews not found");
            }
            List<RespReview> respReviewList = reviewList.stream()
                    .map(this::convertToRespReview)
                    .collect(Collectors.toList());
            response.setT(respReviewList);
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


    private RespReview convertToRespReview(Review review) {
        return RespReview.builder()
                .id(review.getId())
                .userName(review.getCustomer().getUser().getUsername())
                .bookTitle(review.getBook().getTitle())
                .review(review.getReview())
                .rating(review.getRating())
                .reviewDate(review.getReviewDate())
                .build();
    }
}
