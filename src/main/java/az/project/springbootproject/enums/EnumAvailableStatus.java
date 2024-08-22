package az.project.springbootproject.enums;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.lang.ref.PhantomReference;

@NoArgsConstructor
@AllArgsConstructor
public enum EnumAvailableStatus {
    ACTIVE(1), DEACTIVE(0);

    public int value;
}
