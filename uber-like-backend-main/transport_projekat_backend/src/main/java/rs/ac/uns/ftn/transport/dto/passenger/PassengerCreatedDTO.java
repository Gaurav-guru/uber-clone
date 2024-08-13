package rs.ac.uns.ftn.transport.dto.passenger;

import lombok.Data;

@Data
public class PassengerCreatedDTO {

    private Integer id;
    private String name;
    private String surname;
    private String profilePicture;
    private String telephoneNumber;
    private String email;
    private String address;
}
