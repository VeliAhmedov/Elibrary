package az.edu.elibrary.service;

import az.edu.elibrary.dto.request.ReqGenre;
import az.edu.elibrary.dto.response.RespGenre;
import az.edu.elibrary.dto.response.Response;

import java.util.List;

public interface GenreService {
    Response<List<RespGenre>> getGenreList(String token);
    Response<RespGenre> getById(Long genreId,String token);
    Response create(ReqGenre reqGenre,String token);
    Response<RespGenre> update(Long genreId, ReqGenre reqGenre,String token);
    Response delete(Long genreId,String token);
}
