package rs.ac.uns.ftn.transport.dto.review;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rs.ac.uns.ftn.transport.model.DriverReview;
import rs.ac.uns.ftn.transport.model.VehicleReview;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
public class ReviewRideDTO {
    private Set<DriverReviewDTO> driverReview;
    private Set<VehicleReviewDTO> vehicleReview;

    public ReviewRideDTO(Set<DriverReview> driverReview, Set<VehicleReview> vehicleReview) {
        this.driverReview = new HashSet<>();
        this.vehicleReview = new HashSet<>();
        for (DriverReview dr :
                driverReview) {
            this.driverReview.add(new DriverReviewDTO(dr));
        }
        for (VehicleReview vr:
             vehicleReview) {
            this.vehicleReview.add(new VehicleReviewDTO(vr));
        }
    }
}
