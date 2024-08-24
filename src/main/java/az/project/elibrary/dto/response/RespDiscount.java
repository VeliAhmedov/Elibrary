package az.project.elibrary.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
public class RespDiscount {
    private Long id;
    private String name;
    private BigDecimal discountPercentage;
    private Date startDate;
    private Date endDate;
}
