package az.edu.elibrary.service.impl;

import az.edu.elibrary.dto.request.ReqGenre;
import az.edu.elibrary.dto.response.RespGenre;
import az.edu.elibrary.dto.response.RespStatus;
import az.edu.elibrary.dto.response.Response;
import az.edu.elibrary.entity.Genre;
import az.edu.elibrary.enums.EnumAvailableStatus;
import az.edu.elibrary.exception.ExceptionConstants;
import az.edu.elibrary.exception.LibraryException;
import az.edu.elibrary.repository.GenreRepository;
import az.edu.elibrary.service.GenreService;
import az.edu.elibrary.utils.Utility;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;
    private final Utility utility;
    private static final Logger LOGGER = LoggerFactory.getLogger(GenreServiceImpl.class);

    @Override
    public Response<List<RespGenre>> getGenreList(String token) {
        Response<List<RespGenre>> response = new Response<>();
        try {
            utility.checkToken(token);
            LOGGER.info("getGenreList request has been made with token: " + token);
            List<Genre> genreList = genreRepository.findAllByActive(EnumAvailableStatus.ACTIVE.value);
            if (genreList.isEmpty()) {
                LOGGER.warn("No active genres found.");
                throw new LibraryException(ExceptionConstants.GENRE_NOT_FOUND, "Genre not found");
            }
            List<RespGenre> respGenreList = genreList.stream()
                    .map(this::convert)
                    .collect(Collectors.toList());
            response.setT(respGenreList);
            LOGGER.info("Genre list successfully retrieved: " + respGenreList);
            response.setStatus(RespStatus.getSuccessMessage());
        } catch (LibraryException ex) {
            ex.printStackTrace();
            LOGGER.error("LibraryException occurred in getGenreList: ", ex);
            response.setStatus(new RespStatus(ex.getCode(), ex.getMessage()));
        } catch (Exception ex) {
            ex.printStackTrace();
            LOGGER.error("Exception occurred in getGenreList: ", ex);
            response.setStatus(new RespStatus(ExceptionConstants.INTERNAL_EXCEPTION, "Internal Exception"));
        }
        return response;
    }

    @Override
    public Response<RespGenre> getById(Long genreId, String token) {
        Response<RespGenre> response = new Response<>();
        try {
            utility.checkToken(token);
            LOGGER.info("getById request has been made with genreId: " + genreId + " and token: " + token);
            if (genreId == null) {
                LOGGER.warn("Invalid genreId provided.");
                throw new LibraryException(ExceptionConstants.INVALID_REQUEST_DATA, "Invalid request data");
            }
            Genre genre = genreRepository.findGenreByIdAndActive(genreId, EnumAvailableStatus.ACTIVE.value);
            if (genre == null) {
                LOGGER.warn("Genre not found for genreId: " + genreId);
                throw new LibraryException(ExceptionConstants.GENRE_NOT_FOUND, "Genre not found");
            }
            RespGenre respGenre = convert(genre);
            response.setT(respGenre);
            LOGGER.info("Genre retrieved successfully: " + respGenre);
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
    public Response create(ReqGenre reqGenre, String token) {
        Response response = new Response<>();
        try {
            utility.checkToken(token);
            LOGGER.info("Create genre request has been made with token: " + token + " and reqGenre: " + reqGenre);
            String genreName = reqGenre.getGenreName();
            if (genreName == null) {
                LOGGER.warn("Genre name is null in create request.");
                throw new LibraryException(ExceptionConstants.GENRE_NAME_NOT_FOUND, "Invalid request data");
            }
            Genre genre = Genre.builder()
                    .genreName(genreName)
                    .description(reqGenre.getDescription())
                    .build();
            genreRepository.save(genre);
            LOGGER.info("Genre created successfully: " + genre);
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
    public Response<RespGenre> update(Long genreId, ReqGenre reqGenre, String token) {
        Response<RespGenre> response = new Response<>();
        try {
            utility.checkToken(token);
            LOGGER.info("Update genre request has been made with genreId: " + genreId + " and token: " + token);
            if (genreId == null || reqGenre.getGenreName() == null) {
                LOGGER.warn("Invalid genreId or genreName provided in update request.");
                throw new LibraryException(ExceptionConstants.INVALID_REQUEST_DATA, "Invalid request data");
            }
            Genre genre = genreRepository.findGenreByIdAndActive(genreId, EnumAvailableStatus.ACTIVE.value);
            if (genre == null) {
                LOGGER.warn("Genre not found for genreId: " + genreId);
                throw new LibraryException(ExceptionConstants.GENRE_NOT_FOUND, "Genre not found");
            }
            genre.setGenreName(reqGenre.getGenreName());
            genre.setDescription(reqGenre.getDescription());
            genre = genreRepository.save(genre);
            RespGenre respGenre = convert(genre);
            response.setT(respGenre);
            LOGGER.info("Genre updated successfully: " + respGenre);
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
    public Response delete(Long genreId, String token) {
        Response response = new Response<>();
        try {
            utility.checkToken(token);
            LOGGER.info("Delete genre request has been made with genreId: " + genreId + " and token: " + token);
            if (genreId == null) {
                LOGGER.warn("Invalid genreId provided in delete request.");
                throw new LibraryException(ExceptionConstants.INVALID_REQUEST_DATA, "Invalid request data");
            }
            Genre genre = genreRepository.findGenreByIdAndActive(genreId, EnumAvailableStatus.ACTIVE.value);
            if (genre == null) {
                LOGGER.warn("Genre not found for genreId: " + genreId);
                throw new LibraryException(ExceptionConstants.GENRE_NOT_FOUND, "Genre not found");
            }
            genre.setActive(EnumAvailableStatus.DEACTIVE.value);
            genreRepository.save(genre);
            LOGGER.info("Genre deleted successfully: " + genre);
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
    private RespGenre convert(Genre genre) {
        return RespGenre.builder()
                .id(genre.getId())
                .genreName(genre.getGenreName())
                .description(genre.getDescription())
                .build();
    }
}
