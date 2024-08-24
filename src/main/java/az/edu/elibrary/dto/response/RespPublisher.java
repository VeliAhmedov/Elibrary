package az.edu.elibrary.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RespPublisher {
    private long id;
    private String publisherName;
    private String publisherLocation;

}
