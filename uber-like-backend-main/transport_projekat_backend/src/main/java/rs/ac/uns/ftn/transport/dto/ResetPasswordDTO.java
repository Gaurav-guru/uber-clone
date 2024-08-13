package rs.ac.uns.ftn.transport.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordDTO {
    @Pattern(regexp = "^(?=.*\\d)(?=.*[A-Z])(?!.*[^a-zA-Z0-9@#$^+=])(.{8,15})$", message = "{format}")
    @NotBlank(message = "{required}")
    private String newPassword;

    @Pattern(regexp = "^[0-9]{1,6}$", message = "{format}")
    @NotBlank(message = "{required}")
    private String code;
}
