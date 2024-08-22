package az.project.springbootproject.service;

import az.project.springbootproject.dto.request.ReqPublisher;
import az.project.springbootproject.dto.response.RespPublisher;
import az.project.springbootproject.dto.response.Response;

import java.util.List;

public interface PublisherService {
    Response<List<RespPublisher>> getPublisherList(String token);
    Response<RespPublisher> getById(Long publisherId,String token);
    Response create(ReqPublisher reqPublisher,String token);
    Response<RespPublisher> update(Long publisherId, ReqPublisher reqPublisher,String token);
    Response delete(Long publisherId,String token);
}
