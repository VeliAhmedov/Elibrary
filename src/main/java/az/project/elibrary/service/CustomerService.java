package az.project.elibrary.service;

import az.project.elibrary.dto.request.ReqCustomer;
import az.project.elibrary.dto.request.ReqCustomerBalance;
import az.project.elibrary.dto.response.RespCustomer;
import az.project.elibrary.dto.response.Response;

import java.util.List;

public interface CustomerService {
    Response<List<RespCustomer>> getCustomerList(String token);

    Response<RespCustomer> getById(Long customerId, String token);

    Response create(ReqCustomer reqCustomer, String token);

    Response<RespCustomer> update(ReqCustomer reqCustomer,String token);

    Response delete(Long customerId,String token);

    Response<RespCustomer> increaseBalance(ReqCustomerBalance reqCustomerBalance, String token);
}
