package rs.ac.uns.ftn.transport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import rs.ac.uns.ftn.transport.model.Driver;

import java.util.Optional;
import java.util.Set;

public interface DriverRepository extends JpaRepository<Driver, Integer> {

    Optional<Driver> getDriverByVehicle_Id(@Param("id") Integer id);
    Set<Driver> getAllByIsActiveTrue();
}
