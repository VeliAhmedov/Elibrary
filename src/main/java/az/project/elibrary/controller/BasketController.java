package az.project.elibrary.controller;

import az.project.elibrary.dto.request.ReqBasket;
import az.project.elibrary.dto.response.RespBasket;
import az.project.elibrary.dto.response.Response;
import az.project.elibrary.service.BasketService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/basket")
@RequiredArgsConstructor
public class BasketController {
    private final BasketService basketService;

    @GetMapping("/list")
    public Response<List<RespBasket>> getAllBaskets(@RequestHeader String token) {
        return basketService.getBasketList(token);
    }

    @GetMapping("/{id}")
    public Response<RespBasket> getBasketById(@PathVariable Long id,@RequestHeader String token) {
        return basketService.getBasketById(id,token);
    }

    @PostMapping("/create")
    public Response<RespBasket> createBasket(@RequestBody ReqBasket reqBasket,@RequestHeader String token) {
        return basketService.createBasket(reqBasket,token);
    }

    @GetMapping("/customer/{customerId}")
    public Response<List<RespBasket>> getBasketsByCustomerId(@PathVariable Long customerId,@RequestHeader String token) {
        return basketService.getBasketsByCustomerId(customerId,token);
    }
    @GetMapping("/book/{bookId}")
    public Response<List<RespBasket>> getBasketsByBookId(@PathVariable Long bookId, @RequestHeader String token) {
        return basketService.getBasketsByBookId(bookId, token);
    }


    @PutMapping("/remove/{id}")
    public Response<RespBasket> removeBasket(@PathVariable("id") Long id,@RequestHeader String token ) {
        return basketService.removeBasket(id,token);
    }
}
