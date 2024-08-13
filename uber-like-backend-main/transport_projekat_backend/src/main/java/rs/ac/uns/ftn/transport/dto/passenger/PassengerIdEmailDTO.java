package rs.ac.uns.ftn.transport.dto.passenger;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PassengerIdEmailDTO {

    @Min(value=1,message="{id.notValid}")
    private Integer id;

    @Email(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "{format}")
    @Length(max = 255, message = "{maxLength}")
    @NotBlank(message = "{required}")
    private String email;
}
