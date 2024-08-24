package az.edu.elibrary.controller;

import az.edu.elibrary.dto.request.ReqDiscount;
import az.edu.elibrary.dto.response.RespDiscount;
import az.edu.elibrary.dto.response.Response;
import az.edu.elibrary.service.DiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/discount")
@RequiredArgsConstructor
public class DiscountController {

    private final DiscountService discountService;

    @GetMapping("/list")
    public Response<List<RespDiscount>> getDiscountList(@RequestHeader String token) {
        return discountService.getDiscountList(token);
    }

    @GetMapping("/{id}")
    public Response<RespDiscount> getById(@PathVariable Long id,@RequestHeader String token) {
        return discountService.getById(id,token);
    }

    @PostMapping("/create")
    public Response create(@RequestBody ReqDiscount reqDiscount,@RequestHeader String token) {
        return discountService.create(reqDiscount,token);
    }

    @PutMapping("/update/{id}")
    public Response<RespDiscount> update(@PathVariable Long id, @RequestBody ReqDiscount reqDiscount,@RequestHeader String token) {
        return discountService.update(id, reqDiscount,token);
    }

    @PutMapping("/delete/{id}")
    public Response delete(@PathVariable Long id,@RequestHeader String token) {
        return discountService.delete(id,token);
    }
}
