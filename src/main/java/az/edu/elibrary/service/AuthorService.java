package az.edu.elibrary.service;

import az.edu.elibrary.dto.request.ReqAuthor;
import az.edu.elibrary.dto.response.RespAuthor;
import az.edu.elibrary.dto.response.Response;

import java.util.List;

public interface AuthorService {
    Response<List<RespAuthor>> getAuthorList(String token);
    Response<RespAuthor> getById(Long authorId,String token);
    Response create(ReqAuthor reqAuthor,String token);
    Response<RespAuthor> update(Long authorId,ReqAuthor reqAuthor,String token);
    Response delete(Long authorId,String token);
}
