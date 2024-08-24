package az.edu.elibrary.service;

import az.edu.elibrary.dto.request.ReqTransaction;
import az.edu.elibrary.dto.response.RespTransaction;
import az.edu.elibrary.dto.response.Response;

import java.util.List;

public interface TransactionService {
    Response<RespTransaction> createTransaction(ReqTransaction reqTransaction, String token);
    Response<RespTransaction> getTransactionById(Long id,String token);
    Response<List<RespTransaction>> getTransactionsByCustomerId(Long customerId,String token);
    Response<List<RespTransaction>> getAllTransactions(String token);
    Response<RespTransaction> refundTransaction(Long transactionId,String token);

    Response<RespTransaction> getTransactionByBasketId(Long basketId, String token);
}
