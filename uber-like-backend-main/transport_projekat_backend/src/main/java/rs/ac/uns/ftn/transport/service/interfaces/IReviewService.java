package rs.ac.uns.ftn.transport.service.interfaces;

import rs.ac.uns.ftn.transport.dto.review.ReviewRideDTO;
import rs.ac.uns.ftn.transport.model.DriverReview;
import rs.ac.uns.ftn.transport.model.VehicleReview;

import java.util.Set;

public interface IReviewService {
    VehicleReview saveVehicleReview(VehicleReview review);
    Set<VehicleReview> getVehicleReviewsofVehicle(Integer id);
    Set<DriverReview> getDriverReviewsofDriver(Integer id);
    DriverReview saveDriverReview(DriverReview driverReview);
    ReviewRideDTO getReviewsForRide(Integer rideId);

}
