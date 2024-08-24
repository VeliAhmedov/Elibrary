package az.project.elibrary.controller;

import az.project.elibrary.dto.request.ReqTransaction;
import az.project.elibrary.dto.response.RespTransaction;
import az.project.elibrary.dto.response.Response;
import az.project.elibrary.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/create")
    public Response<RespTransaction> createTransaction(@RequestBody ReqTransaction reqTransaction, @RequestHeader String token) {
        return transactionService.createTransaction(reqTransaction, token);
    }

    @GetMapping("/{id}")
    public Response<RespTransaction> getTransactionById(@PathVariable Long id, @RequestHeader String token) {
        return transactionService.getTransactionById(id, token);
    }

    @GetMapping("/customer/{customerId}")
    public Response<List<RespTransaction>> getTransactionsByCustomerId(@PathVariable Long customerId, @RequestHeader String token) {
        return transactionService.getTransactionsByCustomerId(customerId, token);
    }

    @GetMapping("/list")
    public Response<List<RespTransaction>> getAllTransactions( @RequestHeader String token) {
        return transactionService.getAllTransactions(token);
    }


    @PostMapping("/refund/{transactionId}")
    public Response<RespTransaction> refundTransaction(@PathVariable Long transactionId,@RequestHeader String token) {
        return transactionService.refundTransaction(transactionId, token);
    }
    @GetMapping("/basket/{basketId}")
    public Response<RespTransaction> getTransactionByBasketId(@PathVariable Long basketId, @RequestHeader String token){
        return transactionService.getTransactionByBasketId(basketId, token);
    }
}
