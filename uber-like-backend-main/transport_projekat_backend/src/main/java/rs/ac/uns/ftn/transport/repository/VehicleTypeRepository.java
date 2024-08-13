package rs.ac.uns.ftn.transport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import rs.ac.uns.ftn.transport.model.VehicleType;

public interface VehicleTypeRepository extends JpaRepository<VehicleType, Integer> {
    VehicleType findByName(@Param("name") String name);
}