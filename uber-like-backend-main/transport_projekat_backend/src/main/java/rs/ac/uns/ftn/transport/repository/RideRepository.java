package rs.ac.uns.ftn.transport.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rs.ac.uns.ftn.transport.model.Ride;
import rs.ac.uns.ftn.transport.model.enumerations.RideStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RideRepository extends JpaRepository<Ride,Integer> {

    @Query("SELECT r FROM Ride r LEFT JOIN r.passengers pass " +
            "WHERE pass.id = ?1")
    Page<Ride> findPassenger(Integer passengerId, Pageable page);

    Page<Ride> findByPassengers_Id(Integer passengerId, Pageable page);
    Optional<Ride> findByDriver_IdAndStatus(Integer driverId, RideStatus status);

    Optional<Ride> findByPassengers_IdAndStatus(Integer passengerId, RideStatus status);

    Page<Ride> findAllByDriver_Id(@Param("id") Integer id, Pageable page);
    Page<Ride> findAllByDriver_IdAndStartTimeIsAfterAndEndTimeIsBefore(@Param("id") Integer id, @Param("from") LocalDateTime start, @Param("to") LocalDateTime end, Pageable page);
    Page<Ride> findAllByDriver_IdAndStartTimeIsAfter(@Param("id") Integer id, @Param("from") LocalDateTime start, Pageable page);
    Page<Ride> findAllByDriver_IdAndEndTimeIsBefore(@Param("id") Integer id, @Param("to") LocalDateTime end, Pageable page);

    Page<Ride> findAllByPassengers_Id(@Param("id") Integer id, Pageable page);

    Page<Ride> findAllByPassengers_IdAndStartTimeIsAfterAndEndTimeIsBefore(@Param("id") Integer id, @Param("from") LocalDateTime start, @Param("to") LocalDateTime end, Pageable page);

    Page<Ride> findAllByPassengers_IdAndStartTimeIsAfter(@Param("id") Integer id, @Param("from") LocalDateTime start, Pageable page);

    Page<Ride> findAllByPassengers_IdAndEndTimeIsBefore(@Param("id") Integer id, @Param("to") LocalDateTime end, Pageable page);

    List<Ride> findByStatus(RideStatus status);

    Optional<Ride> findFirstByDriver_IdAndScheduledTimeIsAfter(Integer driverId, LocalDateTime now);
    Optional<Ride> findFirstByDriver_IdAndScheduledTimeIsBeforeOrderByScheduledTimeDesc(Integer driverId, LocalDateTime now);

    @Query("SELECT COUNT(r) FROM Ride r JOIN r.passengers p WHERE p.id = :passengerId AND r.endTime >= :rideDate AND r.endTime < :nextDay AND r.status = 'FINISHED'")
    Integer countRidesByPassengerIdAndRideDate(@Param("passengerId") Integer passengerId,
                                               @Param("rideDate") LocalDateTime rideDate,
                                               @Param("nextDay") LocalDateTime nextDay);

    @Query("SELECT SUM(l.distanceInKm) FROM Ride r join r.locations l JOIN r.passengers p WHERE p.id = :passengerId AND r.endTime >= :rideDate AND r.endTime < :nextDay AND r.status = 'FINISHED'")
    Double sumDistanceByPassengerIdAndRideDate(@Param("passengerId") Integer passengerId,
                                               @Param("rideDate") LocalDateTime rideDate,
                                               @Param("nextDay") LocalDateTime nextDay);

    @Query("SELECT SUM(r.totalCost) FROM Ride r JOIN r.passengers p WHERE p.id = :passengerId AND r.endTime >= :rideDate AND r.endTime < :nextDay AND r.status = 'FINISHED'")
    Double sumPriceByPassengerIdAndRideDate(@Param("passengerId") Integer passengerId,
                                            @Param("rideDate") LocalDateTime rideDate,
                                            @Param("nextDay") LocalDateTime nextDay);
}


