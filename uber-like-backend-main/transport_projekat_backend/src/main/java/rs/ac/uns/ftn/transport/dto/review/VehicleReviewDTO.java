package rs.ac.uns.ftn.transport.dto.review;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rs.ac.uns.ftn.transport.dto.passenger.PassengerIdEmailDTO;
import rs.ac.uns.ftn.transport.model.Ride;
import rs.ac.uns.ftn.transport.model.User;
import rs.ac.uns.ftn.transport.model.Vehicle;
import rs.ac.uns.ftn.transport.model.VehicleReview;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleReviewDTO {
    private Integer id;
    private PassengerIdEmailDTO reviewer;
    private String comment;
    private Integer rating;

    public VehicleReviewDTO(VehicleReview vehicleReview){
        this.id = vehicleReview.getId();
        this.comment = vehicleReview.getComment();
        this.rating = vehicleReview.getRating();
        this.reviewer = new PassengerIdEmailDTO(vehicleReview.getReviewer().getId(),vehicleReview.getReviewer().getEmail());
    }
}
