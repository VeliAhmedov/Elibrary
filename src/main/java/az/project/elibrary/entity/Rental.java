package az.project.elibrary.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;

import java.util.Date;

@Data
@Entity
@Table(name = "rental")
@DynamicInsert
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rental {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rental_seq")
    @SequenceGenerator(name = "rental_seq", sequenceName = "rental_seq", allocationSize = 1)
    private long id;

    @ManyToOne
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;


    //Changing to CUSTOMER_Id DOES create error looks this
    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "rental_date")
    private Date rentalDate;

    @Column(name = "due_date")
    private Date dueDate;

    @Column(name = "return_date")
    private Date returnDate;

    @Column(length = 20)
    @ColumnDefault("'ONGOING'")
    private String rentalStatus;

    @Column(name = "late_fee")
    private Double lateFee;

    @CreationTimestamp
    private Date dataDate;

    @ColumnDefault(value = "1")
    private Integer active;
    @PrePersist
    public void prePersist() {
        if (lateFee == null) {
            lateFee = 0.0;
        }
    }
}
