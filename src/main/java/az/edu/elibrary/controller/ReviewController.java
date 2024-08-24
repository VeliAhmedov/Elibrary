package az.edu.elibrary.controller;

import az.edu.elibrary.dto.request.ReqReview;
import az.edu.elibrary.dto.response.RespReview;
import az.edu.elibrary.dto.response.Response;
import az.edu.elibrary.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/list")
    public Response<List<RespReview>> getAllReviews(@RequestHeader String token) {
        return reviewService.getAllReviews(token);
    }

    @GetMapping("/{id}")
    public Response<RespReview> getReviewById(@PathVariable Long id,@RequestHeader String token) {
        return reviewService.getReviewById(id, token);
    }

    @PostMapping("/create")
    public Response<RespReview> createReview(@RequestBody ReqReview reqReview,@RequestHeader String token) {
        return reviewService.createReview(reqReview, token);
    }

    @PutMapping("/update/{id}")
    public Response<RespReview> updateReview(@PathVariable Long id, @RequestBody ReqReview reqReview,@RequestHeader String token) {
        return reviewService.updateReview(id, reqReview, token);
    }

    @PutMapping("/delete/{id}")
    public Response<RespReview> deleteReview(@PathVariable Long id,@RequestHeader String token) {
        return reviewService.deleteReview(id, token);
    }

    @GetMapping("/customer/{customerId}")
    public Response<List<RespReview>> getReviewsByCustomerId(@PathVariable Long customerId,@RequestHeader String token) {
        return reviewService.getReviewsByCustomerId(customerId, token);
    }
    @GetMapping("/book/{bookId}")
    public Response<List<RespReview>> getReviewsByBookId(@PathVariable Long bookId,@RequestHeader String token) {
        return reviewService.getReviewsByBookId(bookId, token);
    }

}
