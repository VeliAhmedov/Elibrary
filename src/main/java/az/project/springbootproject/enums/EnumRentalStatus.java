package az.project.springbootproject.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum EnumRentalStatus {
    ONGOING("Ongoing"),
    OVERDUE("Overdue"),
    RETURNED("Returned"),
    CANCELED("Canceled"),
    OVERDUE_RETURNED("Overdue_Returned");

    private String value;
}
