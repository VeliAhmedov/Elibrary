package az.project.elibrary.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum EnumTransactionStatus {
    FINISHED("Finished"),
    REFUNDED("Refunded");

    private String value;
}
