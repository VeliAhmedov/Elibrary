package az.project.elibrary.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReqAuthor {
    private Long id;
    private String name;
    private String bio;
}