package az.project.elibrary.dto.response;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class RespGenre {
    private long id;
    private String genreName;
    private String description;
}
