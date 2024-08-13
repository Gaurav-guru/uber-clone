package rs.ac.uns.ftn.transport.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rs.ac.uns.ftn.transport.model.*;
import rs.ac.uns.ftn.transport.model.enumerations.RideStatus;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RideDTO {
    Integer id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double totalCost;
    private Driver driver;
    private Set<Passenger> passengers;
    private Set<Route> locations;
    private Integer estimatedTimeInMinutes;
    private Set<Review> reviews;
    private RideStatus status;
    private Rejection rejection;
    private Boolean isPanicPressed;
    private Boolean babyTransport;
    private Boolean petTransport;
    private VehicleType vehicleType;
}
