package rs.ac.uns.ftn.transport.dto.passenger;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PassengerOneDayReportDTO {
    private Integer numberOfRides;
    private Double totalDistance;
    private Double totalCost;
}