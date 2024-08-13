package rs.ac.uns.ftn.transport.dto.panic;

import lombok.Data;
import rs.ac.uns.ftn.transport.dto.UserDTO;
import rs.ac.uns.ftn.transport.dto.passenger.PassengerDTO;
import rs.ac.uns.ftn.transport.dto.ride.RideCreatedDTO;
import rs.ac.uns.ftn.transport.model.Passenger;

import java.time.LocalDateTime;

@Data
public class ExtendedPanicDTO {

    private Integer id;
    private UserDTO user;
    private RideCreatedDTO ride;
    private LocalDateTime time;
    private String reason;
}
