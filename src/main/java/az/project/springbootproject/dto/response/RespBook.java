package az.project.springbootproject.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

@Data
@Builder
public class RespBook {
    private long id;
    private String title;
    private String authorName;
    private String publisherName;
    private Set<String> genreNames;
    private Integer pages;
    private Double price;
    private Integer stock;
    private Integer heldBooks;
    private Integer rentedBooks;
}
