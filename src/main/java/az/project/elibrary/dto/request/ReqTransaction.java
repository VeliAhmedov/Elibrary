package az.project.elibrary.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReqTransaction {
    private Long customerId;
    private Long basketId;
}
