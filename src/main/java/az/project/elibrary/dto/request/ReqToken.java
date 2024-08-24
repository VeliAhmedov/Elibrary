package az.project.elibrary.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReqToken {
    private Long userId;
    String token;
}
