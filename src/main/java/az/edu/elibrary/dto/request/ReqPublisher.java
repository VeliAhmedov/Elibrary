package az.edu.elibrary.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReqPublisher {
    private Long id;
    private String publisherName;
    private String publisherLocation;
}
