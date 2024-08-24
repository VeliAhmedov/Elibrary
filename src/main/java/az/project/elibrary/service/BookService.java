package az.project.elibrary.service;

import az.project.elibrary.dto.request.ReqBook;
import az.project.elibrary.dto.response.RespBook;
import az.project.elibrary.dto.response.Response;

import java.util.List;

public interface BookService {
    Response<List<RespBook>> getBookList(String token);
    Response<RespBook> getById(Long bookId,String token);
    Response<RespBook> create(ReqBook reqBook,String token);
    Response<RespBook> update(ReqBook reqBook,String token);
    Response delete(Long bookId,String token);
}
