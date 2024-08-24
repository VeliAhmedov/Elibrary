package az.project.elibrary.dto.request;

import az.project.elibrary.enums.EnumCurrency;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReqCustomerBalance {
    private Long customerId;
    private Double amount;
    private EnumCurrency currency;
}
