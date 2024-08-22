package az.project.springbootproject.controller;

import az.project.springbootproject.dto.request.ReqCustomer;
import az.project.springbootproject.dto.request.ReqCustomerBalance;
import az.project.springbootproject.dto.request.ReqToken;
import az.project.springbootproject.dto.response.RespCustomer;
import az.project.springbootproject.dto.response.Response;
import az.project.springbootproject.service.CustomerService;
import ch.qos.logback.core.pattern.util.RegularEscapeUtil;
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
