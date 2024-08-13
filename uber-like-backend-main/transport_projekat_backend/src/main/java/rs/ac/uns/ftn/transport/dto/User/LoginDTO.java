package rs.ac.uns.ftn.transport.dto.User;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LoginDTO {
    private String email;
    private String password;
}
