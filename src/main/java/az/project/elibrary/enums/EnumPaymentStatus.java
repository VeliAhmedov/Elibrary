package az.project.elibrary.enums;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public enum EnumPaymentStatus {
    NOT_PAID("Not Paid"),
    PAID("Paid"),
    // didn't know what to use, duplicate created here
    REFUNDED("Refunded");

    private String value;
}
