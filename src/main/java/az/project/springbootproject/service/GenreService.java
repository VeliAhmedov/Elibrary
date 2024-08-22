package az.project.springbootproject.service;

import az.project.springbootproject.dto.request.ReqGenre;
import az.project.springbootproject.dto.response.RespGenre;
import az.project.springbootproject.dto.response.Response;

import java.util.List;

public interface GenreService {
    Response<List<RespGenre>> getGenreList(String token);
    Response<RespGenre> getById(Long genreId,String token);
    Response create(ReqGenre reqGenre,String token);
    Response<RespGenre> update(Long genreId, ReqGenre reqGenre,String token);
    Response delete(Long genreId,String token);
}
