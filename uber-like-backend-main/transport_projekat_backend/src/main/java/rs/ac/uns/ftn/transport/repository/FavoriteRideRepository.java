package rs.ac.uns.ftn.transport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rs.ac.uns.ftn.transport.model.FavoriteRide;

import java.util.Set;

public interface FavoriteRideRepository extends JpaRepository<FavoriteRide,Integer> {
    @Query("SELECT fr FROM FavoriteRide fr JOIN fr.passengers p WHERE p.id = :passengerId")
    Set<FavoriteRide> findAllByPassengerId(@Param("passengerId") Integer passengerId);
}
