package az.project.springbootproject.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RespBasket {
    private Long id;
    private String bookTitle;
    private String userName;
    private Integer amount;
    private Double totalPrice;
    private String paymentStatus;
}
