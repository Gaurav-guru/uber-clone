package rs.ac.uns.ftn.transport.dto.driver;

import javax.persistence.Basic;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverDTO {
    private Integer id;

    @Length(max = 100, message = "{maxLength}")
    @NotBlank(message = "{required}")
    private String name;

    @Length(max = 100, message = "{maxLength}")
    @NotBlank(message = "{required}")
    private String surname;

    private String profilePicture;

    @Length(max = 18, message = "{maxLength}")
    private String telephoneNumber;

    @Email(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "{format}")
    @Length(max = 100, message = "{maxLength}")
    @NotBlank(message = "{required}")
    private String email;

    @Length(max = 100, message = "{maxLength}")
    @NotBlank(message = "{required}")
    private String address;
}
