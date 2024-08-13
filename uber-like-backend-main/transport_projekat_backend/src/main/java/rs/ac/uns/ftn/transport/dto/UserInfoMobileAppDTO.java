package rs.ac.uns.ftn.transport.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfoMobileAppDTO {

    private int id;
    private String name;
    private String surname;
    private String email;
    private String profilePicture;
    private String role;
}
