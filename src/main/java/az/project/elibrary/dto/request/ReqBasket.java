package az.project.elibrary.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReqBasket {
    private Long customerId;
    private Long bookId;
    private Integer amount;
}
