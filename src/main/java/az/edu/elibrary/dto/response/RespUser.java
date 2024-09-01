package az.edu.elibrary.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RespUser {
    private String username;
    private RespToken respToken;
    private String Role;
}
