package rs.ac.uns.ftn.transport.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rs.ac.uns.ftn.transport.dto.ride.IncomingRideSimulationDTO;
import rs.ac.uns.ftn.transport.model.FavoriteRide;
import rs.ac.uns.ftn.transport.model.Rejection;
import rs.ac.uns.ftn.transport.model.Ride;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface IFavoriteRideService {

    FavoriteRide save(FavoriteRide ride);
    void delete(Integer id);
    Set<FavoriteRide> findAll();
    Set<FavoriteRide> findAllByPassenger(Integer id);
}
