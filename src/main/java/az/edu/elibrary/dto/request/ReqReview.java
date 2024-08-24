package az.edu.elibrary.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReqReview {
    private Long customerId;
    private Long bookId;
    private String review;
    private Integer rating;
}
