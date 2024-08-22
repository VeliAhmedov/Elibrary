package az.project.springbootproject.service;

import az.project.springbootproject.dto.request.ReqCustomer;
import az.project.springbootproject.dto.request.ReqCustomerBalance;
import az.project.springbootproject.dto.request.ReqToken;
import az.project.springbootproject.dto.response.RespCustomer;
import az.project.springbootproject.dto.response.Response;

import java.util.List;

public interface CustomerService {
    Response<List<RespCustomer>> getCustomerList(String token);

    Response<RespCustomer> getById(Long customerId, String token);

    Response create(ReqCustomer reqCustomer, String token);

    Response<RespCustomer> update(ReqCustomer reqCustomer,String token);

    Response delete(Long customerId,String token);

    Response<RespCustomer> increaseBalance(ReqCustomerBalance reqCustomerBalance, String token);
}
