package az.edu.elibrary.service.impl;

import az.edu.elibrary.dto.request.ReqAuthor;
import az.edu.elibrary.dto.response.RespAuthor;
import az.edu.elibrary.dto.response.RespStatus;
import az.edu.elibrary.dto.response.Response;
import az.edu.elibrary.entity.Author;
import az.edu.elibrary.enums.EnumAvailableStatus;
import az.edu.elibrary.exception.ExceptionConstants;
import az.edu.elibrary.exception.LibraryException;
import az.edu.elibrary.repository.AuthorRepository;
import az.edu.elibrary.service.AuthorService;
import az.edu.elibrary.utils.Utility;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;
    private final Utility utility;
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerServiceImpl.class);
    @Override
    public Response<List<RespAuthor>> getAuthorList(String token) {
        Response<List<RespAuthor>> response = new Response<>();
        try {
            utility.checkToken(token);
            LOGGER.info("getAuthorList request has been done with token: "+token);
            List<Author> authorList = authorRepository.findAllByActive(EnumAvailableStatus.ACTIVE.value);
            if (authorList.isEmpty()) {
                LOGGER.warn("author is empty, there is no active Author");
                throw new LibraryException(ExceptionConstants.AUTHOR_NOT_FOUND, "Author not found");
            }
            List<RespAuthor> respAuthorList = authorList.stream()
                    .map(this::convert)
                    .collect(Collectors.toList());
            response.setT(respAuthorList);
            LOGGER.info("getAuthor list has been successfully returned: "+ respAuthorList);
            response.setStatus(RespStatus.getSuccessMessage());
        } catch (LibraryException ex) {
            ex.printStackTrace();
            LOGGER.error("LibraryException occurred in authorList : ", ex);
            response.setStatus(new RespStatus(ex.getCode(), ex.getMessage()));
        } catch (Exception ex) {
            ex.printStackTrace();
            LOGGER.error("Exception occurred in authorList : ", ex);
            response.setStatus(new RespStatus(ExceptionConstants.INTERNAL_EXCEPTION, "Internal Exception"));
        }
        return response;
    }
    @Override
    public Response<RespAuthor> getById(Long authorId,String token) {
        Response<RespAuthor> response = new Response<>();
        try {
            utility.checkToken(token);
            LOGGER.info("getById in author request has been done with token: "+token);
            if (authorId == null) {
                LOGGER.warn("there has been send invalid request for AuthorId in getById with authorId of "+ authorId);
                throw new LibraryException(ExceptionConstants.INVALID_REQUEST_DATA, "Invalid request data");
            }
            Author author = authorRepository.findAuthorByIdAndActive(authorId, EnumAvailableStatus.ACTIVE.value);
            if (author == null) {
                LOGGER.warn("authorId of " +authorId+" isn't active");
                throw new LibraryException(ExceptionConstants.AUTHOR_NOT_FOUND, "Author not found");
            }
            RespAuthor respAuthor = convert(author);
            response.setT(respAuthor);
            LOGGER.info("getById in author has been successfully returned response "+ respAuthor);
            response.setStatus(RespStatus.getSuccessMessage());
        } catch (LibraryException ex) {
            ex.printStackTrace();
            LOGGER.error("LibraryException occurred in getById in Author : ", ex);
            response.setStatus(new RespStatus(ex.getCode(), ex.getMessage()));
        } catch (Exception ex) {
            ex.printStackTrace();
            LOGGER.error("Exception occurred in getById in Author: ", ex);
            response.setStatus(new RespStatus(ExceptionConstants.INTERNAL_EXCEPTION, "Internal Exception"));
        }
        return response;
    }
    @Override
    public Response create(ReqAuthor reqAuthor,String token) {
        Response response = new Response<>();
        try {
            utility.checkToken(token);
            LOGGER.info("Create in author request has been done with token: "+token);
            String name = reqAuthor.getName();
            if (name == null) {
                LOGGER.warn("author name has been ignored");
                throw new LibraryException(ExceptionConstants.INVALID_REQUEST_DATA, "Invalid request data");
            }
            Author author = Author.builder()
                    .name(name)
                    .bio(reqAuthor.getBio())
                    .build();
            authorRepository.save(author);
            LOGGER.info("Author has  successfully created data: "+ author);
            response.setStatus(RespStatus.getSuccessMessage());
        } catch (LibraryException ex) {
            ex.printStackTrace();
            LOGGER.error("LibraryException occurred in create in Author : ", ex);
            response.setStatus(new RespStatus(ex.getCode(), ex.getMessage()));
        } catch (Exception ex) {
            ex.printStackTrace();
            LOGGER.error("Exception occurred in create in Author : ", ex);
            response.setStatus(new RespStatus(ExceptionConstants.INTERNAL_EXCEPTION, "Internal Exception"));
        }
        return response;
    }
    @Override
    public Response<RespAuthor> update(Long authorId, ReqAuthor reqAuthor,String token) {
        Response<RespAuthor> response = new Response<>();
        try {
            utility.checkToken(token);
            LOGGER.info("update in author request has been done with token: "+token);
            if (authorId == null || reqAuthor.getName() == null) {
                LOGGER.warn("Either author name has been ignored or AuthorId of "+authorId+" doesn't exist");
                throw new LibraryException(ExceptionConstants.AUTHOR_NAME_NOT_FOUND, "Author name doesn't exist");
            }
            Author author = authorRepository.findAuthorByIdAndActive(authorId, EnumAvailableStatus.ACTIVE.value);
            if (author == null) {
                LOGGER.warn("AuthorId of "+ authorId+" isn't active");
                throw new LibraryException(ExceptionConstants.AUTHOR_NOT_FOUND, "Author not found");
            }
            author.setName(reqAuthor.getName());
            author.setBio(reqAuthor.getBio());
            author = authorRepository.save(author);
            RespAuthor respAuthor = convert(author);
            response.setT(respAuthor);
            LOGGER.info("authorId of"+ authorId+" has successfully been updated to"+ respAuthor);
            response.setStatus(RespStatus.getSuccessMessage());
        } catch (LibraryException ex) {
            ex.printStackTrace();
            LOGGER.error("LibraryException occurred in update in Author : ", ex);
            response.setStatus(new RespStatus(ex.getCode(), ex.getMessage()));
        } catch (Exception ex) {
            ex.printStackTrace();
            LOGGER.error("Exception occurred in update in Author : ", ex);
            response.setStatus(new RespStatus(ExceptionConstants.INTERNAL_EXCEPTION, "Internal Exception"));
        }
        return response;
    }
    @Override
    public Response delete(Long authorId,String token) {
        Response response = new Response<>();
        try {
            utility.checkToken(token);
            LOGGER.info("delete in author request has been done with token: "+token);
            if (authorId == null) {
                LOGGER.warn("AuthorId of "+ authorId+" doesn't exist");
                throw new LibraryException(ExceptionConstants.INVALID_REQUEST_DATA, "Invalid request data");
            }
            Author author = authorRepository.findAuthorByIdAndActive(authorId, EnumAvailableStatus.ACTIVE.value);
            if (author == null) {
                LOGGER.warn("AuthorId of "+ authorId+"  is already inactive");
                throw new LibraryException(ExceptionConstants.AUTHOR_NOT_FOUND, "Author not found");
            }
            author.setActive(EnumAvailableStatus.DEACTIVE.value);
            authorRepository.save(author);
            LOGGER.info("authorId of "+ authorId+" has been successfully deactivated");
            response.setStatus(RespStatus.getSuccessMessage());
        } catch (LibraryException ex) {
            ex.printStackTrace();
            LOGGER.error("LibraryException occurred in delete in Author : ", ex);
            response.setStatus(new RespStatus(ex.getCode(), ex.getMessage()));
        } catch (Exception ex) {
            ex.printStackTrace();
            LOGGER.error("Exception occurred in delete in Author : ", ex);
            response.setStatus(new RespStatus(ExceptionConstants.INTERNAL_EXCEPTION, "Internal Exception"));
        }
        return response;
    }
    private RespAuthor convert(Author author) {
        return RespAuthor.builder()
                .id(author.getId())
                .name(author.getName())
                .bio(author.getBio())
                .build();
    }
}
