package az.edu.elibrary.service.impl;

import az.edu.elibrary.dto.request.ReqPublisher;
import az.edu.elibrary.dto.response.RespPublisher;
import az.edu.elibrary.dto.response.RespStatus;
import az.edu.elibrary.dto.response.Response;
import az.edu.elibrary.entity.Publisher;
import az.edu.elibrary.enums.EnumAvailableStatus;
import az.edu.elibrary.exception.ExceptionConstants;
import az.edu.elibrary.exception.LibraryException;
import az.edu.elibrary.repository.PublisherRepository;
import az.edu.elibrary.service.PublisherService;
import az.edu.elibrary.utils.Utility;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublisherServiceImpl implements PublisherService {
    private final PublisherRepository publisherRepository;
    private final Utility utility;
    private static final Logger LOGGER = LoggerFactory.getLogger(PublisherServiceImpl.class);

    @Override
    public Response<List<RespPublisher>> getPublisherList(String token) {
        Response<List<RespPublisher>> response = new Response<>();
        try {
            utility.checkToken(token);
            LOGGER.info("getPublisherList request has been made with token: " + token);
            List<Publisher> publisherList = publisherRepository.findAllByActive(EnumAvailableStatus.ACTIVE.value);
            if (publisherList.isEmpty()) {
                LOGGER.warn("No active publishers found.");
                throw new LibraryException(ExceptionConstants.PUBLISHER_NOT_FOUND, "Publisher not found");
            }
            List<RespPublisher> respPublisherList = publisherList.stream()
                    .map(this::convert)
                    .collect(Collectors.toList());
            response.setT(respPublisherList);
            LOGGER.info("Publisher list successfully retrieved: " + respPublisherList);
            response.setStatus(RespStatus.getSuccessMessage());
        } catch (LibraryException ex) {
            ex.printStackTrace();
            LOGGER.error("LibraryException occurred in getPublisherList: ", ex);
            response.setStatus(new RespStatus(ex.getCode(), ex.getMessage()));
        } catch (Exception ex) {
            ex.printStackTrace();
            LOGGER.error("Exception occurred in getPublisherList: ", ex);
            response.setStatus(new RespStatus(ExceptionConstants.INTERNAL_EXCEPTION, "Internal Exception"));
        }
        return response;
    }

    @Override
    public Response<RespPublisher> getById(Long publisherId, String token) {
        Response<RespPublisher> response = new Response<>();
        try {
            utility.checkToken(token);
            LOGGER.info("getById request has been made with publisherId: " + publisherId + " and token: " + token);
            if (publisherId == null) {
                LOGGER.warn("Invalid publisherId provided.");
                throw new LibraryException(ExceptionConstants.INVALID_REQUEST_DATA, "Invalid request data");
            }
            Publisher publisher = publisherRepository.findPublisherByIdAndActive(publisherId, EnumAvailableStatus.ACTIVE.value);
            if (publisher == null) {
                LOGGER.warn("Publisher not found for publisherId: " + publisherId);
                throw new LibraryException(ExceptionConstants.PUBLISHER_NOT_FOUND, "Publisher not found");
            }
            RespPublisher respPublisher = convert(publisher);
            response.setT(respPublisher);
            LOGGER.info("Publisher retrieved successfully: " + respPublisher);
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
    public Response create(ReqPublisher reqPublisher, String token) {
        Response response = new Response<>();
        try {
            utility.checkToken(token);
            LOGGER.info("Create publisher request has been made with token: " + token + " and reqPublisher: " + reqPublisher);
            String publisherName = reqPublisher.getPublisherName();
            if (publisherName == null) {
                LOGGER.warn("Publisher name is null in create request.");
                throw new LibraryException(ExceptionConstants.PUBLISHER_NAME_NOT_FOUND, "Publisher name not found");
            }
            Publisher publisher = Publisher.builder()
                    .publisherName(publisherName)
                    .publisherLocation(reqPublisher.getPublisherLocation())
                    .build();
            publisherRepository.save(publisher);
            LOGGER.info("Publisher created successfully: " + publisher);
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
    public Response<RespPublisher> update(Long publisherId, ReqPublisher reqPublisher, String token) {
        Response<RespPublisher> response = new Response<>();
        try {
            utility.checkToken(token);
            LOGGER.info("Update publisher request has been made with publisherId: " + publisherId + " and token: " + token);
            if (publisherId == null || reqPublisher.getPublisherName() == null) {
                LOGGER.warn("Invalid publisherId or publisherName provided in update request.");
                throw new LibraryException(ExceptionConstants.INVALID_REQUEST_DATA, "Invalid request data");
            }
            Publisher publisher = publisherRepository.findPublisherByIdAndActive(publisherId, EnumAvailableStatus.ACTIVE.value);
            if (publisher == null) {
                LOGGER.warn("Publisher not found for publisherId: " + publisherId);
                throw new LibraryException(ExceptionConstants.PUBLISHER_NOT_FOUND, "Publisher not found");
            }
            publisher.setPublisherName(reqPublisher.getPublisherName());
            publisher.setPublisherLocation(reqPublisher.getPublisherLocation());
            publisher = publisherRepository.save(publisher);
            RespPublisher respPublisher = convert(publisher);
            response.setT(respPublisher);
            LOGGER.info("Publisher updated successfully: " + respPublisher);
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
    public Response delete(Long publisherId, String token) {
        Response response = new Response<>();
        try {
            utility.checkToken(token);
            LOGGER.info("Delete publisher request has been made with publisherId: " + publisherId + " and token: " + token);
            if (publisherId == null) {
                LOGGER.warn("Invalid publisherId provided in delete request.");
                throw new LibraryException(ExceptionConstants.INVALID_REQUEST_DATA, "Invalid request data");
            }
            Publisher publisher = publisherRepository.findPublisherByIdAndActive(publisherId, EnumAvailableStatus.ACTIVE.value);
            if (publisher == null) {
                LOGGER.warn("Publisher not found for publisherId: " + publisherId);
                throw new LibraryException(ExceptionConstants.PUBLISHER_NOT_FOUND, "Publisher not found");
            }
            publisher.setActive(EnumAvailableStatus.DEACTIVE.value);
            publisherRepository.save(publisher);
            LOGGER.info("Publisher deleted successfully: " + publisher);
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

    private RespPublisher convert(Publisher publisher) {
        return RespPublisher.builder()
                .id(publisher.getId())
                .publisherName(publisher.getPublisherName())
                .publisherLocation(publisher.getPublisherLocation())
                .build();
    }
}
