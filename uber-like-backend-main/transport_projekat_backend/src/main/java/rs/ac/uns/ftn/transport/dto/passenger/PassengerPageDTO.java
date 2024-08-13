package rs.ac.uns.ftn.transport.dto.passenger;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rs.ac.uns.ftn.transport.dto.passenger.PassengerCreatedDTO;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PassengerPageDTO {

    public Long totalCount;
    public Set<PassengerCreatedDTO> results;
}
