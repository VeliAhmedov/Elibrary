package az.project.springbootproject.entity;

import az.project.springbootproject.enums.EnumPaymentStatus;
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
@Table(name = "baskets")
@DynamicInsert
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Basket {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "basket_seq")
    @SequenceGenerator(name = "basket_seq", sequenceName = "basket_seq", allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customerId", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    private Integer amount;

    @Column(length = 20)
    @ColumnDefault("'selected'")
    private String progress;

    @Transient
    private Double totalPrice;

    @Column(length = 20)
    @ColumnDefault("'NOT_PAID'")
    private String paymentStatus;

    @CreationTimestamp
    private Date dataDate;

    @ColumnDefault(value = "1")
    private Integer active;

    @PostLoad
    public void calculateTotalPrice() {
        if (book != null && amount != null) {
            this.totalPrice = book.getPrice() * amount;
        }
    }
}
