package az.project.elibrary.controller;

import az.project.elibrary.dto.request.ReqCustomer;
import az.project.elibrary.dto.request.ReqCustomerBalance;
import az.project.elibrary.dto.response.RespCustomer;
import az.project.elibrary.dto.response.Response;
import az.project.elibrary.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping(value = "/list")
    public Response<List<RespCustomer>> getCustomerList(@RequestHeader String token){
        return customerService.getCustomerList(token);
    }

    @GetMapping("/{id}")
    public Response<RespCustomer> getById(@PathVariable("id") Long customerId, @RequestHeader String token){
        return customerService.getById(customerId, token);
    }
    @PostMapping("/create")
    public Response create(@RequestBody ReqCustomer reqCustomer, @RequestHeader String token){
        return customerService.create(reqCustomer, token);
    }

    @PutMapping("/update")
    public Response<RespCustomer> update(@RequestBody ReqCustomer reqCustomer, @RequestHeader String token){
        return customerService.update(reqCustomer, token);
    }
    @PutMapping("/delete/{id}")
    public Response delete(@PathVariable("id") Long customerId, @RequestHeader String token){
        return customerService.delete(customerId, token);
    }
    @PostMapping("/increase-balance")
    public Response<RespCustomer> increaseBalance(@RequestBody ReqCustomerBalance reqCustomerBalance, @RequestHeader String token) {
        return customerService.increaseBalance(reqCustomerBalance,token);
    }
}
