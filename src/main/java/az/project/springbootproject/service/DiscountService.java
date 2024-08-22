package az.project.springbootproject.service;

import az.project.springbootproject.dto.request.ReqDiscount;
import az.project.springbootproject.dto.response.RespDiscount;
import az.project.springbootproject.dto.response.Response;
import az.project.springbootproject.entity.Discount;

import java.util.List;

public interface DiscountService {
    Response<List<RespDiscount>> getDiscountList(String token);
    Response<RespDiscount> getById(Long discountId,String token);
    Response create(ReqDiscount reqDiscount,String token);
    Response<RespDiscount> update(Long discountId, ReqDiscount reqDiscount,String token);
    Response delete(Long discountId,String token);



}
