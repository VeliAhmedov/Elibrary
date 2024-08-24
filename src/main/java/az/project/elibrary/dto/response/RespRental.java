package az.project.elibrary.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class RespRental {
    private Long id;
    private String userName;
    private String bookTitle;
    private Date rentalDate;
    private Date dueDate;
    private Date returnDate;
    private String rentalStatus;
    private Double lateFee;
}
