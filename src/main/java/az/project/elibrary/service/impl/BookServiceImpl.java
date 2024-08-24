package az.project.elibrary.service.impl;

import az.project.elibrary.dto.request.ReqBook;
import az.project.elibrary.dto.response.RespBook;
import az.project.elibrary.dto.response.RespStatus;
import az.project.elibrary.dto.response.Response;
import az.project.elibrary.entity.*;
import az.project.elibrary.enums.EnumAvailableStatus;
import az.project.elibrary.exception.ExceptionConstants;
import az.project.elibrary.exception.LibraryException;
import az.project.elibrary.repository.*;
import az.project.elibrary.service.BookService;
import az.project.elibrary.utils.Utility;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;
    private final GenreRepository genreRepository;
    private final DiscountRepository discountRepository;
    private final Utility utility;
    private static final Logger LOGGER = LoggerFactory.getLogger(PublisherServiceImpl.class);


    @Override
    public Response<List<RespBook>> getBookList(String token) {
        Response<List<RespBook>> response = new Response<>();
        try {
            LOGGER.info("getBookList request has been initiated with token: " + token);
            utility.checkToken(token);
            List<Book> bookList = bookRepository.findAllByActive(EnumAvailableStatus.ACTIVE.value);
            if (bookList.isEmpty()) {
                LOGGER.warn("No active books found.");
                throw new LibraryException(ExceptionConstants.BOOK_NOT_FOUND, "Book not found");
            }
            List<RespBook> respBookList = bookList.stream()
                    .map(this::convert)
                    .collect(Collectors.toList());
            response.setT(respBookList);
            LOGGER.info("getBookList successfully returned response with books: " + respBookList);
            response.setStatus(RespStatus.getSuccessMessage());
        } catch (LibraryException ex) {
            ex.printStackTrace();
            LOGGER.error("LibraryException occurred in getBookList: ", ex);
            response.setStatus(new RespStatus(ex.getCode(), ex.getMessage()));
        } catch (Exception ex) {
            ex.printStackTrace();
            LOGGER.error("Exception occurred in getBookList: ", ex);
            response.setStatus(new RespStatus(ExceptionConstants.INTERNAL_EXCEPTION, "Internal Exception"));
        }
        return response;
    }


    @Override
    public Response<RespBook> getById(Long bookId, String token) {
        Response<RespBook> response = new Response<>();
        try {
            LOGGER.info("getById request initiated with bookId: " + bookId + " and token: " + token);
            utility.checkToken(token);
            if (bookId == null) {
                LOGGER.warn("Invalid request data: bookId is null.");
                throw new LibraryException(ExceptionConstants.INVALID_REQUEST_DATA, "invalid request data");
            }
            Book book = bookRepository.findBookByIdAndActive(bookId, EnumAvailableStatus.ACTIVE.value);
            if (book == null) {
                LOGGER.warn("Book with bookId: " + bookId + " not found.");
                throw new LibraryException(ExceptionConstants.BOOK_NOT_FOUND, "Book not found");
            }
            RespBook respBook = convert(book);
            response.setT(respBook);
            LOGGER.info("getById successfully returned response with book: " + respBook);
            response.setStatus(RespStatus.getSuccessMessage());
        } catch (LibraryException ex) {
            ex.printStackTrace();
            LOGGER.error("LibraryException occurred in getById: ", ex);
            response.setStatus(new RespStatus(ex.getCode(), ex.getMessage()));
        } catch (Exception ex) {
            ex.printStackTrace();
            LOGGER.error("Exception occurred in getById: ", ex);
            response.setStatus(new RespStatus(ExceptionConstants.INTERNAL_EXCEPTION, "Internal Exception"));
        }
        return response;
    }


    @Override
    public Response<RespBook> create(ReqBook reqBook, String token) {
        Response<RespBook> response = new Response<>();
        try {
            LOGGER.info("create request initiated with token: " + token);
            utility.checkToken(token);
            String title = reqBook.getTitle();
            if (title == null) {
                LOGGER.warn("Title not found in request.");
                throw new LibraryException(ExceptionConstants.BOOK_TITLE_BOT_FOUND, "title not found");
            }
            Author author = authorRepository.findAuthorByIdAndActive(reqBook.getAuthorId(), EnumAvailableStatus.ACTIVE.value);
            if (author == null) {
                LOGGER.warn("Author with ID " + reqBook.getAuthorId() + " not found.");
                throw new LibraryException(ExceptionConstants.AUTHOR_NOT_FOUND, "Author not found");
            }
            Publisher publisher = publisherRepository.findPublisherByIdAndActive(reqBook.getPublisherId(), EnumAvailableStatus.ACTIVE.value);
            if (publisher == null) {
                LOGGER.warn("Publisher with ID " + reqBook.getPublisherId() + " not found.");
                throw new LibraryException(ExceptionConstants.PUBLISHER_NOT_FOUND, "Publisher not found");
            }
            List<Genre> genres = reqBook.getGenreIds().stream()
                    .map(id -> {
                        Genre genre = genreRepository.findGenreByIdAndActive(id, EnumAvailableStatus.ACTIVE.value);
                        if (genre == null) {
                            LOGGER.warn("Genre with ID " + id + " not found.");
                            throw new LibraryException(ExceptionConstants.GENRE_NOT_FOUND, "Genre with ID " + id + " not found");
                        }
                        return genre;
                    })
                    .collect(Collectors.toList());

            Book book = Book.builder()
                    .title(title)
                    .author(author)
                    .publisher(publisher)
                    .genreName(genres)
                    .pages(reqBook.getPages())
                    .price(reqBook.getPrice())
                    .stock(reqBook.getStock())
                    .build();
            book = bookRepository.save(book);

            RespBook respBook = convert(book);
            response.setT(respBook);
            LOGGER.info("Book successfully created with ID: " + book.getId());
            response.setStatus(RespStatus.getSuccessMessage());
        } catch (LibraryException ex) {
            ex.printStackTrace();
            LOGGER.error("LibraryException occurred in create: ", ex);
            response.setStatus(new RespStatus(ex.getCode(), ex.getMessage()));
        } catch (Exception ex) {
            ex.printStackTrace();
            LOGGER.error("Exception occurred in create: ", ex);
            response.setStatus(new RespStatus(ExceptionConstants.INTERNAL_EXCEPTION, "Internal Exception"));
        }
        return response;
    }


    @Override
    public Response<RespBook> update(ReqBook reqBook, String token) {
        Response<RespBook> response = new Response<>();
        try {
            LOGGER.info("update request initiated with token: " + token + " and bookId: " + reqBook.getId());
            utility.checkToken(token);
            if (reqBook.getId() == null) {
                LOGGER.warn("Invalid request data: bookId is null.");
                throw new LibraryException(ExceptionConstants.INVALID_REQUEST_DATA, "invalid request data");
            }
            Book book = bookRepository.findBookByIdAndActive(reqBook.getId(), EnumAvailableStatus.ACTIVE.value);
            if (book == null) {
                LOGGER.warn("Book with ID: " + reqBook.getId() + " not found.");
                throw new LibraryException(ExceptionConstants.BOOK_NOT_FOUND, "Book not found");
            }

            Author author = authorRepository.findAuthorByIdAndActive(reqBook.getAuthorId(), EnumAvailableStatus.ACTIVE.value);
            if (author == null) {
                LOGGER.warn("Author with ID " + reqBook.getAuthorId() + " not found.");
                throw new LibraryException(ExceptionConstants.AUTHOR_NOT_FOUND, "Author not found");
            }

            Publisher publisher = publisherRepository.findPublisherByIdAndActive(reqBook.getPublisherId(), EnumAvailableStatus.ACTIVE.value);
            if (publisher == null) {
                LOGGER.warn("Publisher with ID " + reqBook.getPublisherId() + " not found.");
                throw new LibraryException(ExceptionConstants.PUBLISHER_NOT_FOUND, "Publisher not found");
            }

            List<Genre> genres = reqBook.getGenreIds().stream()
                    .map(id -> {
                        Genre genre = genreRepository.findGenreByIdAndActive(id, EnumAvailableStatus.ACTIVE.value);
                        if (genre == null) {
                            LOGGER.warn("Genre with ID " + id + " not found.");
                            throw new LibraryException(ExceptionConstants.GENRE_NOT_FOUND, "Genre with ID " + id + " not found or inactive");
                        }
                        return genre;
                    })
                    .collect(Collectors.toList());

            book.setTitle(reqBook.getTitle());
            book.setAuthor(author);
            book.setPublisher(publisher);
            book.setGenreName(genres);
            book.setPages(reqBook.getPages());
            book.setPrice(reqBook.getPrice());
            book.setStock(reqBook.getStock());
            book = bookRepository.save(book);
            RespBook respBook = convert(book);
            response.setT(respBook);
            LOGGER.info("Book with ID: " + reqBook.getId() + " successfully updated.");
            response.setStatus(RespStatus.getSuccessMessage());
        } catch (LibraryException ex) {
            ex.printStackTrace();
            LOGGER.error("LibraryException occurred in update: ", ex);
            response.setStatus(new RespStatus(ex.getCode(), ex.getMessage()));
        } catch (Exception ex) {
            ex.printStackTrace();
            LOGGER.error("Exception occurred in update: ", ex);
            response.setStatus(new RespStatus(ExceptionConstants.INTERNAL_EXCEPTION, "Internal Exception"));
        }
        return response;
    }


    @Override
    public Response delete(Long bookId, String token) {
        Response response = new Response<>();
        try {
            LOGGER.info("delete request initiated with token: " + token + " and bookId: " + bookId);
            utility.checkToken(token);
            if (bookId == null) {
                LOGGER.warn("Invalid request data: bookId is null.");
                throw new LibraryException(ExceptionConstants.INVALID_REQUEST_DATA, "invalid request data");
            }
            Book book = bookRepository.findBookByIdAndActive(bookId, EnumAvailableStatus.ACTIVE.value);
            if (book == null) {
                LOGGER.warn("Book with ID: " + bookId + " not found.");
                throw new LibraryException(ExceptionConstants.BOOK_NOT_FOUND, "Book not found");
            }
            book.setActive(EnumAvailableStatus.DEACTIVE.value);
            bookRepository.save(book);
            LOGGER.info("Book with ID: " + bookId + " successfully deactivated.");
            response.setStatus(RespStatus.getSuccessMessage());
        } catch (LibraryException ex) {
            ex.printStackTrace();
            LOGGER.error("LibraryException occurred in delete: ", ex);
            response.setStatus(new RespStatus(ex.getCode(), ex.getMessage()));
        } catch (Exception ex) {
            ex.printStackTrace();
            LOGGER.error("Exception occurred in delete: ", ex);
            response.setStatus(new RespStatus(ExceptionConstants.INTERNAL_EXCEPTION, "Internal Exception"));
        }
        return response;
    }


    private RespBook convert(Book book) {
        // given current date of active valid discount will be here
        Date currentDate = new Date();
        //getting active discounts here
        List<Discount> validDiscounts = discountRepository.findByActiveAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                EnumAvailableStatus.ACTIVE.value, currentDate, currentDate);

        Double discountedPrice = book.getPrice(); // will be updated if there is valid discount if not then just stay same
        //if valid discount there is this here will
        if (!validDiscounts.isEmpty()) {
            //didn't delete this before I created validation for same interval discount
            Discount activeDiscount = validDiscounts.get(0);
            BigDecimal discountPercentage = activeDiscount.getDiscountPercentage().divide(BigDecimal.valueOf(100));
            discountedPrice = book.getPrice() * (1 - discountPercentage.doubleValue());
        }
        //this is for did use formatting, for price
        String formatPriceWithString = String.format("%.2f", discountedPrice);
        Double formattedPrice = Double.valueOf(formatPriceWithString);

        return RespBook.builder()
                .id(book.getId())
                .title(book.getTitle())
                .authorName(book.getAuthor().getName())
                .publisherName(book.getPublisher().getPublisherName())
                .genreNames(book.getGenreName().stream().map(Genre::getGenreName).collect(Collectors.toSet()))
                .pages(book.getPages())
                .price(formattedPrice) // result will be here
                .stock(book.getStock())
                .heldBooks(book.getHeldBooks())
                .rentedBooks(book.getRentedBooks())
                .build();
    }


}
