package az.project.springbootproject.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReqBasket {
    private Long customerId;
    private Long bookId;
    private Integer amount;
}
