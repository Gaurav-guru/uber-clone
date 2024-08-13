package rs.ac.uns.ftn.transport.dto.ride;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rs.ac.uns.ftn.transport.dto.RideDTO;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RidePage2DTO {
    public Long totalCount;
    public Set<RideDTO> results;
}
