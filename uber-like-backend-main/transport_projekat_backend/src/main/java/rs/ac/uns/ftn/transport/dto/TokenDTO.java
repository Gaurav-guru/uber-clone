package rs.ac.uns.ftn.transport.dto;

import lombok.Data;
import rs.ac.uns.ftn.transport.model.User;

@Data
public class TokenDTO {
    String accessToken;
    String refreshToken;

    public TokenDTO(String s, String s1) {
        this.accessToken = s;
        this.refreshToken = s1;
    }
}
