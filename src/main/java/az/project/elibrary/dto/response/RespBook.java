package az.project.elibrary.dto.response;

import lombok.Builder;
import lombok.Data;

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
