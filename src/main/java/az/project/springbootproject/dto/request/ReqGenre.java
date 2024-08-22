package az.project.springbootproject.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReqGenre {
    private Long id;
    private String genreName;
    private String description;
}