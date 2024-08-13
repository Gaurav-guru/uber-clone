package rs.ac.uns.ftn.transport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import rs.ac.uns.ftn.transport.model.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {
    Vehicle getVehicleByDriver_Id(@Param("id") Integer id);
}