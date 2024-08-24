package az.edu.elibrary.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class RespCustomer {

    private long id;
    private String name;
    private String surname;
    private String address;
    private Date dob ;
    private String phone;
    private String pin;
    private String libraryCardNumber;
    private Double balance;
    private String userName;
}
