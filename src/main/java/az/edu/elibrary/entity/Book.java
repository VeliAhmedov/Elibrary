package az.edu.elibrary.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;

import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "book")
@DynamicInsert
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "book_seq")
    @SequenceGenerator(name = "book_seq", sequenceName = "book_seq", allocationSize = 1)
    private long id;

    @Column(length = 100, nullable = false)
    private String title;

    @ManyToMany
    @JoinTable(
            name = "book_genre",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private List<Genre> genreName;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;

    @ManyToOne
    @JoinColumn(name = "publisher_id", nullable = false)
    private Publisher publisher;

    private Integer pages;
    private Double price;
    private Integer stock;

    private Integer heldBooks; // these are books that is in process for rent

    private Integer rentedBooks; // these are books that are currently rented
    @CreationTimestamp
    private Date dataDate;

    @ColumnDefault(value = "1")
    private Integer active;

    //we insert condition and main data to held and available in first book creation
    @PrePersist
    public void prePersist() {
        //both will be null when created
        if (heldBooks == null) {
            heldBooks = 0;
        }
        if (rentedBooks==null){
            rentedBooks = 0;
        }
    }

//    @PrePersist: Before insertion.
//    @PostLoad: After loading.
//    @PostUpdate: After updating.
//    @PostPersist: After insertion.
}
