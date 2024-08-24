package az.project.elibrary.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class RespReview {
    private Long id;
    private String userName;
    private String bookTitle;
    private String review;
    private Integer rating;
    private Date reviewDate;
}
