package az.project.elibrary.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReqUser {
    private String username;
    private String password;
    private String Role;
    private String fullName;
}
