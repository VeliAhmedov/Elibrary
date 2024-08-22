package az.project.springbootproject.service;

import az.project.springbootproject.dto.request.ReqReview;
import az.project.springbootproject.dto.response.RespReview;
import az.project.springbootproject.dto.response.Response;
import az.project.springbootproject.entity.Review;

import java.util.List;

public interface ReviewService {

    Response<List<RespReview>> getAllReviews(String token);

    Response<RespReview> getReviewById(Long id,String token);

    Response<RespReview> createReview(ReqReview reqReview,String token);

    Response<RespReview> updateReview(Long id, ReqReview reqReview,String token);

    Response<RespReview> deleteReview(Long id,String token);

    Response<List<RespReview>> getReviewsByCustomerId(Long customerId,String token);
    Response<List<RespReview>> getReviewsByBookId(Long bookId,String token);




}
