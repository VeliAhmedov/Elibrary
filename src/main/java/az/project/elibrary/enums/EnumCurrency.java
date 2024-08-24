package az.project.elibrary.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum EnumCurrency  {
    AZN("AZN"), USD("USD"), EUR("EUR");

    public String  value;
}
