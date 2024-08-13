package rs.ac.uns.ftn.transport.service;

import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import rs.ac.uns.ftn.transport.dto.ride.IncomingRideSimulationDTO;
import rs.ac.uns.ftn.transport.model.Driver;
import rs.ac.uns.ftn.transport.model.Rejection;
import rs.ac.uns.ftn.transport.model.Ride;
import rs.ac.uns.ftn.transport.model.Route;
import rs.ac.uns.ftn.transport.model.enumerations.RideStatus;
import rs.ac.uns.ftn.transport.repository.DriverRepository;
import rs.ac.uns.ftn.transport.repository.RideRepository;
import rs.ac.uns.ftn.transport.service.interfaces.IFindingDriverService;
import rs.ac.uns.ftn.transport.service.interfaces.IRideService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Service
public class RideServiceImpl implements IRideService {

    private final RideRepository rideRepository;
    private final DriverRepository driverRepository;
    private final MessageSource messageSource;
    private final IFindingDriverService findingDriverService;
    private final EstimatesService estimatesService;
    private TaskScheduler scheduleReservation;
    private final int RESERVATION_MINIMUM_TIME_MINUTES = 30;

    public RideServiceImpl(RideRepository rideRepository,
                           DriverRepository driverRepository,
                           MessageSource messageSource,
                           IFindingDriverService findingDriverService,
                           EstimatesService estimatesService, TaskScheduler scheduler) {
        this.rideRepository = rideRepository;
        this.driverRepository = driverRepository;
        this.messageSource = messageSource;
        this.findingDriverService = findingDriverService;
        this.estimatesService = estimatesService;
        this.scheduleReservation = scheduler;
    }

    @Override
    public Ride save(Ride ride, boolean isReservation,int passengerId) {
        Optional<Ride> pending = this.rideRepository.findByPassengers_IdAndStatus(passengerId,RideStatus.PENDING);
        if(pending.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,messageSource.getMessage("order.alreadyOrdered", null, Locale.getDefault()));
        }
        if(!isReservation) ride.setScheduledTime(LocalDateTime.now());
        Driver suitable = this.findingDriverService.findSuitableDriver(ride,isReservation);
        if(suitable == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,messageSource.getMessage("order.couldNotFindDriver", null, Locale.getDefault()));
        }
        ride.setDriver(suitable);
        ride.setStatus(RideStatus.PENDING);
        double totalDistance = 0;
        for(Route route : ride.getLocations()) {
            totalDistance += this.estimatesService.calculateDistance(route.getDeparture(),route.getDestination());
        }
        ride.setEstimatedTimeInMinutes((int)Math.round(this.estimatesService.getEstimatedTime(totalDistance)));
        ride.setTotalCost(this.estimatesService.getEstimatedPrice(ride.getVehicleType(),totalDistance));
        return rideRepository.save(ride);
    }

    @Override
    public void reserve(Ride ride,int passengerId){
        if(Math.abs(Duration.between(LocalDateTime.now(),ride.getScheduledTime()).toMinutes()) > 5 * 60){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,messageSource.getMessage("order.scheduleTimeMax", null, Locale.getDefault()));
        }
        if(ride.getScheduledTime().isBefore(LocalDateTime.now())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,messageSource.getMessage("order.scheduleTimeBefore", null, Locale.getDefault()));
        }
        long betweenNowAndScheduledRideMinutes = Duration.between(LocalDateTime.now(),ride.getScheduledTime()).toMinutes();
        if(betweenNowAndScheduledRideMinutes < RESERVATION_MINIMUM_TIME_MINUTES) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,messageSource.getMessage("order.scheduleTime", null, Locale.getDefault()));
        }
        this.scheduleReserving(ride,passengerId);
    }


    private void scheduleReserving(Ride order,int passengerId) {
        ScheduledExecutorService localExecutor = Executors.newSingleThreadScheduledExecutor();
        scheduleReservation = new ConcurrentTaskScheduler(localExecutor);
        Date toSchedule = Date.from(order.getScheduledTime().minus(this.RESERVATION_MINIMUM_TIME_MINUTES + 1, ChronoUnit.MINUTES)
                .atZone(ZoneId.systemDefault())
                .toInstant());
        Runnable scheduledTask = new Runnable() {
            private Ride order;
            public Runnable init(Ride ride) {
                this.order = ride;
                return this;
            }
            @Override
            public void run() {
                RideServiceImpl.this.save(this.order,true,passengerId);
            }
        }.init(order);
        scheduleReservation.schedule(scheduledTask, toSchedule);
    }

    @Override
    public Ride findActiveForDriver(Integer driverId) {
        Optional<Ride> active = rideRepository.findByDriver_IdAndStatus(driverId,RideStatus.ACTIVE);
        if(active.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return active.get();
    }

    @Override
    public Ride findActiveForPassenger(Integer passengerId) {
        Optional<Ride> active = rideRepository.findByPassengers_IdAndStatus(passengerId,RideStatus.ACTIVE);
        if(active.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return active.get();
    }

    @Override
    public Page<Ride> findPassenger(Integer passengerId, Pageable page) {
        Page<Ride> rides = rideRepository.findByPassengers_Id(passengerId, page);
        return rides;
    }

    @Override
    public Ride findOne(Integer id) {

        Optional<Ride> active = rideRepository.findById(id);
        if(active.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return active.get();
    }

    public Page<Ride> findAllByDriver_Id(Integer id, Pageable page) {
        return rideRepository.findAllByDriver_Id(id, page);
    }

    public Page<Ride> findAllByDriver_IdAndStartTimeIsAfterAndEndTimeIsBefore(Integer id, LocalDateTime start, LocalDateTime end, Pageable page) {
        return rideRepository.findAllByDriver_IdAndStartTimeIsAfterAndEndTimeIsBefore(id, start, end, page);
    }

    public Page<Ride> findAllByDriver_IdAndStartTimeIsAfter(Integer id, LocalDateTime start, Pageable page) {
        return rideRepository.findAllByDriver_IdAndStartTimeIsAfter(id, start, page);
    }

    public Page<Ride> findAllByDriver_IdAndEndTimeIsBefore(Integer id, LocalDateTime end, Pageable page) {
        return rideRepository.findAllByDriver_IdAndEndTimeIsBefore(id, end, page);
    }

    @Override
    public Page<Ride> findAllByPassenger_Id(Integer id, Pageable page) {
        return rideRepository.findAllByPassengers_Id(id, page);
    }

    @Override
    public Page<Ride> findAllByPassenger_IdAndStartTimeIsAfterAndEndTimeIsBefore(Integer id, LocalDateTime start, LocalDateTime end, Pageable page) {
        return rideRepository.findAllByPassengers_IdAndStartTimeIsAfterAndEndTimeIsBefore(id, start, end, page);
    }

    @Override
    public Page<Ride> findAllByPassenger_IdAndStartTimeIsAfter(Integer id, LocalDateTime start, Pageable page) {
        return rideRepository.findAllByPassengers_IdAndStartTimeIsAfter(id, start,page);
    }

    @Override
    public Page<Ride> findAllByPassenger_IdAndEndTimeIsBefore(Integer id, LocalDateTime end, Pageable page) {
        return rideRepository.findAllByPassengers_IdAndEndTimeIsBefore(id, end, page);
    }

    @Override
    public Ride cancelRide(Integer id) {
        Optional<Ride> toCancel = rideRepository.findById(id);
        if(toCancel.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,messageSource.getMessage("ride.notFound", null, Locale.getDefault()));
        }
        Ride cancelled = toCancel.get();
        if(cancelled.getStatus() != RideStatus.PENDING && cancelled.getStatus() != RideStatus.ACCEPTED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("cancellation.invalidStatus", null, Locale.getDefault()));
        }
        cancelled.setStatus(RideStatus.CANCELLED);
        rideRepository.save(cancelled);
        return cancelled;
    }

    @Override
    public Ride acceptRide(Integer id) {
        Optional<Ride> toAccept = rideRepository.findById(id);
        if(toAccept.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,messageSource.getMessage("ride.notFound", null, Locale.getDefault()));
        }
        Ride accepted = toAccept.get();
        if(accepted.getStatus() != RideStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("accepting.invalidStatus", null, Locale.getDefault()));
        }
        accepted.setStatus(RideStatus.ACCEPTED);
        rideRepository.save(accepted);
        return accepted;
    }

    @Override
    public Ride endRide(Integer id) {
        Optional<Ride> toEnd = rideRepository.findById(id);
        if(toEnd.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,messageSource.getMessage("ride.notFound", null, Locale.getDefault()));
        }
        Ride ended = toEnd.get();
        if(ended.getStatus() != RideStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("ending.invalidStatus", null, Locale.getDefault()));
        }
        ended.setStatus(RideStatus.FINISHED);
        ended.setEndTime(LocalDateTime.now());
        rideRepository.save(ended);
        return ended;
    }

    @Override
    public Ride startRide(Integer id) {
        Optional<Ride> toStart = rideRepository.findById(id);
        if(toStart.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,messageSource.getMessage("ride.notFound", null, Locale.getDefault()));
        }
        Ride started = toStart.get();
        if(started.getStatus() != RideStatus.ACCEPTED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("starting.invalidStatus", null, Locale.getDefault()));
        }
        started.setStatus(RideStatus.ACTIVE);
        started.setStartTime(LocalDateTime.now());
        rideRepository.save(started);
        return started;
    }

    @Override
    public Ride cancelWithExplanation(Integer rideId, Rejection explanation) {
        Optional<Ride> toReject = rideRepository.findById(rideId);
        if(toReject.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,messageSource.getMessage("ride.notFound", null, Locale.getDefault()));
        }
        Ride rejected = toReject.get();
        if(rejected.getStatus() != RideStatus.PENDING && rejected.getStatus() != RideStatus.ACCEPTED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("cancellation.invalidStatus", null, Locale.getDefault()));
        }
        rejected.setStatus(RideStatus.REJECTED);
        explanation.setRide(rejected);
        explanation.setTimeOfRejection(LocalDateTime.now());
        rejected.setRejection(explanation);
        rideRepository.save(rejected);
        return rejected;

    }

    public List<Ride> getActiveRides() {
        return rideRepository.findByStatus(RideStatus.ACTIVE);
    }

    public Ride saveForSimulation(IncomingRideSimulationDTO dto) {
        Ride ride = new Ride();
        ride.setEstimatedTimeInMinutes(5);
        ride.setStartTime(LocalDateTime.now());
        ride.setEndTime(LocalDateTime.now().plus(5, ChronoUnit.MINUTES));
        Driver driver = driverRepository.findById(dto.getDriverId()).orElseGet(null);
        driver.getVehicle().getCurrentLocation().setLatitude(dto.getLatitude());
        driver.getVehicle().getCurrentLocation().setLongitude(dto.getLongitude());
        ride.setDriver(driver);
        ride.setRejection(null);
        ride.setTotalCost(1234.0);
        ride.setStatus(RideStatus.ACTIVE);
        ride.setVehicleType(driver.getVehicle().getVehicleType());
        return rideRepository.save(ride);
    }
}
