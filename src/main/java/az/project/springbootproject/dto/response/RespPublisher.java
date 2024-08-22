package az.project.springbootproject.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class RespPublisher {
    private long id;
    private String publisherName;
    private String publisherLocation;

}
