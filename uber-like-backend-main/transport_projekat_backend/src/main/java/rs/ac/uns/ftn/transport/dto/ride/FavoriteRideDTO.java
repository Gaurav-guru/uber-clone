package rs.ac.uns.ftn.transport.dto.ride;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rs.ac.uns.ftn.transport.dto.RouteDTO;
import rs.ac.uns.ftn.transport.dto.passenger.PassengerIdEmailDTO;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteRideDTO extends FavoriteRideWithoutIdDTO {
    private Integer id;

}
