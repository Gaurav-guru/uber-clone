package rs.ac.uns.ftn.transport.dto.ride;

import lombok.Data;
import rs.ac.uns.ftn.transport.dto.VehicleSimulationDTO;

@Data
public class OutgoingRideSimulationDTO {
    private int id;
    private VehicleSimulationDTO vehicle;
}
