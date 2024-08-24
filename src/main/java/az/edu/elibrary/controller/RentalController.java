package az.edu.elibrary.controller;

import az.edu.elibrary.dto.response.RespRental;
import az.edu.elibrary.dto.response.Response;
import az.edu.elibrary.service.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rentals")
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;

    @GetMapping("/list")
    public Response<List<RespRental>> listRentals(@RequestHeader String token) {
        return rentalService.listRentals(token);
    }

    @GetMapping("/{id}")
    public Response<RespRental> getRentalById(@PathVariable Long id,@RequestHeader String token) {
        return rentalService.getRentalById(id,token);
    }

    @PutMapping("/return/{rentalId}")
    public Response<RespRental> returnBook(@PathVariable Long rentalId,@RequestHeader String token) {
        return rentalService.returnBook(rentalId,token);
    }

    @GetMapping("/customer/{customerId}")
    public Response<List<RespRental>> getRentalsByCustomerId(@PathVariable Long customerId, @RequestHeader String token) {
        return rentalService.getRentalsByCustomerId(customerId, token);
    }
}
