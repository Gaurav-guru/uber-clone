package rs.ac.uns.ftn.transport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import rs.ac.uns.ftn.transport.model.Driver;
import rs.ac.uns.ftn.transport.model.DriverReview;
import rs.ac.uns.ftn.transport.model.Ride;

import java.util.Set;

public interface DriverReviewRepository extends JpaRepository<DriverReview, Integer> {

    Set<DriverReview> findByDriver(@Param("driver") Driver driver);

    Set<DriverReview> findByCurrentRide(Ride currentRide);
}
