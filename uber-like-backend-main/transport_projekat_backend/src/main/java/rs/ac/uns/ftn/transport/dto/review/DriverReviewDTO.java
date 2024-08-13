package rs.ac.uns.ftn.transport.dto.review;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.access.method.P;
import rs.ac.uns.ftn.transport.dto.passenger.PassengerIdEmailDTO;
import rs.ac.uns.ftn.transport.model.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverReviewDTO {
    private Integer id;
    private Integer rating;
    private String comment;
    private PassengerIdEmailDTO reviewer;

    public DriverReviewDTO(DriverReview driverReview){
        this.id = driverReview.getId();
        this.comment = driverReview.getComment();
        this.rating = driverReview.getRating();
        this.reviewer = new PassengerIdEmailDTO(driverReview.getReviewer().getId(),driverReview.getReviewer().getEmail());
    }
}
