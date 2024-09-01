package az.edu.elibrary.dto.request;

import lombok.Builder;
import lombok.Data;

import javax.xml.transform.sax.SAXTransformerFactory;
import java.util.Date;

@Data
@Builder
public class ReqUser {
    private String username;
    private String password;
    private String Role;
    private String email;
    // Fields needed for Customer creation
    private String name;
    private String surname;
    private Date dob; // Date of Birth
    private String address;
    private String libraryCardNumber;
    private String phone;
    private String pin;
}
