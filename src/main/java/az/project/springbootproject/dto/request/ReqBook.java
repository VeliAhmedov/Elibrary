package az.project.springbootproject.dto.request;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class ReqBook {
    private Long id;
    private String title;
    private Long authorId;
    private Long publisherId;
    private Set<Long> genreIds;
    private Integer pages;
    private Double price;
    private Integer stock;
}
