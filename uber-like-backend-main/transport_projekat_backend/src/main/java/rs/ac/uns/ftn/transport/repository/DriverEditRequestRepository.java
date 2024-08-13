package rs.ac.uns.ftn.transport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.ac.uns.ftn.transport.model.DriverEditRequest;

public interface DriverEditRequestRepository extends JpaRepository<DriverEditRequest, Integer> {
    DriverEditRequest findByDriverId(Integer driverId);
}
