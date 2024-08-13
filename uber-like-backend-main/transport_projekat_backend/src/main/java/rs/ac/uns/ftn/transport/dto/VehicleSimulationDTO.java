package rs.ac.uns.ftn.transport.dto;

import lombok.Data;
import rs.ac.uns.ftn.transport.model.Vehicle;

@Data
public class VehicleSimulationDTO {
    private int id;
    private double latitude;
    private double longitude;

    public VehicleSimulationDTO() {

    }

    public VehicleSimulationDTO(Vehicle vehicle){
        this.id = vehicle.getId();
        this.latitude = vehicle.getCurrentLocation().getLatitude();
        this.longitude = vehicle.getCurrentLocation().getLongitude();
    }
}
