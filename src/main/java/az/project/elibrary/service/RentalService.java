package az.project.elibrary.service;

import az.project.elibrary.dto.response.RespRental;
import az.project.elibrary.dto.response.Response;

import java.util.List;

public interface RentalService {

    Response<List<RespRental>> listRentals(String token);

    Response<RespRental> getRentalById(Long id,String token);
    Response<RespRental> returnBook(Long rentalId,String token);
    Response<List<RespRental>> getRentalsByCustomerId(Long customerId, String token);

}
