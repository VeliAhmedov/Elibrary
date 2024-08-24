package az.edu.elibrary.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class ReqCustomer {
    @JsonProperty("id")
    private Long customerId;
    private String name;
    private String surname;
    private String address;
    private Date dob ;
    private String phone;
    private String pin;
    private String libraryCardNumber;
    private ReqToken reqToken;
    private Long userId;
}
