package rs.ac.uns.ftn.transport.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rs.ac.uns.ftn.transport.dto.ride.IncomingRideSimulationDTO;
import rs.ac.uns.ftn.transport.model.Rejection;
import rs.ac.uns.ftn.transport.model.Ride;
import java.time.LocalDateTime;
import java.util.List;

public interface IRideService {

    Ride save(Ride ride, boolean isReservation, int passengerId);
    void reserve(Ride ride, int passengerId);
    Ride findActiveForDriver(Integer driverId);
    Ride findActiveForPassenger(Integer passengerId);
    Page<Ride> findPassenger(Integer passengerId, Pageable page);
    Ride findOne(Integer id);
    Page<Ride> findAllByDriver_Id(Integer id, Pageable page);
    Page<Ride> findAllByDriver_IdAndStartTimeIsAfterAndEndTimeIsBefore(Integer id, LocalDateTime start, LocalDateTime end, Pageable page);
    Page<Ride> findAllByDriver_IdAndStartTimeIsAfter(Integer id, LocalDateTime start, Pageable page);
    Page<Ride> findAllByDriver_IdAndEndTimeIsBefore(Integer id, LocalDateTime end, Pageable page);
    Page<Ride> findAllByPassenger_Id(Integer id, Pageable page);
    Page<Ride> findAllByPassenger_IdAndStartTimeIsAfterAndEndTimeIsBefore(Integer id, LocalDateTime start, LocalDateTime end, Pageable page);
    Page<Ride> findAllByPassenger_IdAndStartTimeIsAfter(Integer id, LocalDateTime start, Pageable page);
    Page<Ride> findAllByPassenger_IdAndEndTimeIsBefore(Integer id, LocalDateTime end, Pageable page);
    Ride cancelRide(Integer id);
    Ride acceptRide(Integer id);
    Ride endRide(Integer id);
    Ride startRide(Integer id);
    Ride cancelWithExplanation(Integer rideId, Rejection explanation);
    List<Ride> getActiveRides();
    Ride saveForSimulation(IncomingRideSimulationDTO dto);
}
