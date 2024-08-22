package az.project.springbootproject.service;

import az.project.springbootproject.dto.request.ReqBasket;
import az.project.springbootproject.dto.response.RespBasket;
import az.project.springbootproject.dto.response.Response;

import java.util.List;

public interface BasketService {
    Response<List<RespBasket>> getBasketList(String token);
    Response<RespBasket> getBasketById(Long id,String token);
    Response<RespBasket> createBasket(ReqBasket reqBasket,String token);
    Response<List<RespBasket>> getBasketsByCustomerId(Long customerId,String token);
    Response<RespBasket> removeBasket(Long id,String token);

    Response<List<RespBasket>> getBasketsByBookId(Long bookId, String token);
}
