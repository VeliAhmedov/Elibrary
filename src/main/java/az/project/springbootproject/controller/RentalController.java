package az.project.springbootproject.controller;

import az.project.springbootproject.dto.response.RespRental;
import az.project.springbootproject.dto.response.RespStatus;
import az.project.springbootproject.dto.response.Response;
import az.project.springbootproject.service.RentalService;
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
