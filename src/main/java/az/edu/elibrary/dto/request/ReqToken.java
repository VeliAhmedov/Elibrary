package az.edu.elibrary.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReqToken {
    private Long userId;
    String token;
}
