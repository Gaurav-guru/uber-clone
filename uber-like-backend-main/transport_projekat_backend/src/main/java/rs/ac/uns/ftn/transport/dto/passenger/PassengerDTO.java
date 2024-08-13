package rs.ac.uns.ftn.transport.dto.passenger;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PassengerDTO {

    @Length(max = 255, message = "{maxLength}")
    @NotBlank(message = "{required}")
    private String name;
    @Length(max = 255, message = "{maxLength}")
    @NotBlank(message = "{required}")
    private String surname;

    private String profilePicture;

    @Pattern(regexp = "^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$" +
            "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?){2}\\d{3}$" +
            "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?)(\\d{2}[ ]?){2}\\d{2}$", message = "{format}")
    private String telephoneNumber;
    @Email(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "{format}")
    @Length(max = 255, message = "{maxLength}")
    @NotBlank(message = "{required}")
    private String email;
    @Length(max = 255, message = "{maxLength}")
    @NotBlank(message = "{required}")
    private String address;

    @Pattern(regexp = "^(?=.*\\d)(?=.*[A-Z])(?!.*[^a-zA-Z0-9@#$^+=])(.{8,15})$", message = "{format}")
    @NotBlank(message = "{required}")
    private String password;
}