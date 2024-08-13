package rs.ac.uns.ftn.transport.dto.panic;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PanicReasonDTO {

    @Length(max = 255,message = "{maxLength}")
    @NotBlank(message = "{required}")
    private String reason;
}
