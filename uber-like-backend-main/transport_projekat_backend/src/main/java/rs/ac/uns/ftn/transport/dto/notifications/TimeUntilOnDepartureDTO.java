package rs.ac.uns.ftn.transport.dto.notifications;

import lombok.Data;
import rs.ac.uns.ftn.transport.dto.passenger.PassengerIdEmailDTO;

import java.util.List;

@Data
public class TimeUntilOnDepartureDTO {

    List<Integer> passengersIds;
    String timeFormatted;
}
