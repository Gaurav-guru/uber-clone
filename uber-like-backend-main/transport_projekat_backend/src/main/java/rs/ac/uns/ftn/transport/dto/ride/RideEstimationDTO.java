package rs.ac.uns.ftn.transport.dto.ride;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RideEstimationDTO {
    private int estimatedTimeInMinutes;
    private int estimatedCost;
}
