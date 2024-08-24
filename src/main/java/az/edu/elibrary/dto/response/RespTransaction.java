package az.edu.elibrary.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class RespTransaction {
    private Long id;
    private String userName;      // username from user of customer
    private String bookTitle;        // Title of the book from the Basket's Book entity
    private Integer quantity;        // Quantity from the Basket
    private Double transactionAmount;// Total price from the Basket
    private Date transactionDate;
    private String transactionStatus;
}
