package az.project.elibrary.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RespToken {
    private Long userId;
    String token;
}
