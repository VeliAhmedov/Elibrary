package az.edu.elibrary.service;

import az.edu.elibrary.dto.request.ReqPublisher;
import az.edu.elibrary.dto.response.RespPublisher;
import az.edu.elibrary.dto.response.Response;

import java.util.List;

public interface PublisherService {
    Response<List<RespPublisher>> getPublisherList(String token);
    Response<RespPublisher> getById(Long publisherId,String token);
    Response create(ReqPublisher reqPublisher,String token);
    Response<RespPublisher> update(Long publisherId, ReqPublisher reqPublisher,String token);
    Response delete(Long publisherId,String token);
}
