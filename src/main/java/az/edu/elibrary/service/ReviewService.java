package az.edu.elibrary.service;

import az.edu.elibrary.dto.request.ReqReview;
import az.edu.elibrary.dto.response.RespReview;
import az.edu.elibrary.dto.response.Response;

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
