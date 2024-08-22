package az.project.springbootproject.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum EnumProgressStatus {
    SELECTED("Selected"), REMOVED("Removed");

    private String value;
}
