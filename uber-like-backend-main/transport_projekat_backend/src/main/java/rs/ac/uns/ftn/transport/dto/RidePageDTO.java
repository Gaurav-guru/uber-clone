package rs.ac.uns.ftn.transport.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rs.ac.uns.ftn.transport.dto.ride.RideCreatedDTO;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RidePageDTO {
    public Long totalCount;
    public Set<RideCreatedDTO> results;
}