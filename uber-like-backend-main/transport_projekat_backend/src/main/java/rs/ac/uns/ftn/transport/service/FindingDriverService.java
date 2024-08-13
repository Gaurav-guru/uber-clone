package rs.ac.uns.ftn.transport.service;

import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.transport.model.*;
import rs.ac.uns.ftn.transport.model.enumerations.RideStatus;
import rs.ac.uns.ftn.transport.repository.DriverRepository;
import rs.ac.uns.ftn.transport.repository.RideRepository;
import rs.ac.uns.ftn.transport.service.interfaces.IFindingDriverService;
import rs.ac.uns.ftn.transport.service.interfaces.IWorkingHoursService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class FindingDriverService implements IFindingDriverService {

    private final EstimatesService estimatesService;
    private final DriverRepository driverRepository;
    private final RideRepository rideRepository;
    private final IWorkingHoursService workingHoursService;

    public FindingDriverService(EstimatesService estimatesService,
                                DriverRepository driverRepository,
                                RideRepository rideRepository,
                                IWorkingHoursService workingHoursService) {
        this.estimatesService = estimatesService;
        this.driverRepository = driverRepository;
        this.rideRepository = rideRepository;
        this.workingHoursService = workingHoursService;
    }

    @Override
    public Driver findSuitableDriver(Ride order,boolean isReservation) {
        LocalDateTime now = LocalDateTime.now();

        //PHASE 1: Filtering drivers based on status (active/logged in -- inactive)
        Set<Driver> activeDrivers = this.driverRepository.getAllByIsActiveTrue();
        if (activeDrivers.size() == 0) return null; //no logged in drivers
        List<Driver> inRide = new ArrayList<>();
        List<Driver> free = new ArrayList<>();

        //PHASE 2 : Filtering active drivers based on working hours (>=8 hours do not get rides) and vehicle
        // (if vehicle is suitable, driver can go to other stages of filtration) and sorting them
        //according to their current state (ride in progress -- free drivers)
        for (Driver driver : activeDrivers) {
            if(!this.canFitInWorkingHours(order,driver,isReservation)) continue;
            if(driver.getVehicle() == null) continue; //doesn't have assigned vehicle
            if(this.isVehicleSuitable(order,driver.getVehicle())) {
                if(this.isInActiveRide(driver)) inRide.add(driver);
                else free.add(driver);
            }
        }

        //PHASE 3 : Filtering free drivers based on time(if they have a scheduled ride, they should have time for the ordered ride)
        //and if they have previously accepted ride, they should have time to finish it before ordered time
        //Since they do not have a ride going on, they have higher priority for ride assignment
        List<Driver> mostSuitable = new ArrayList<>();
        for(Driver freeDriver : free) {
            boolean hasPreviouslyAcceptedRide = this.hasAcceptedRide(freeDriver,order);
            boolean hasScheduledRide = this.hasScheduledRide(freeDriver, now);
            if(!hasPreviouslyAcceptedRide && !hasScheduledRide) mostSuitable.add(freeDriver);
            else {
                boolean acceptedRideOverlaps = false;
                boolean hasTimeBeforeScheduled = true;
                if(hasPreviouslyAcceptedRide) {
                    acceptedRideOverlaps = this.doesPreviouslyAcceptedRideOverlap(order,freeDriver,false);
                }
                if(hasScheduledRide) {
                    Ride scheduled = this.rideRepository.findFirstByDriver_IdAndScheduledTimeIsAfter(freeDriver.getId(), now).get();
                    hasTimeBeforeScheduled = this.hasTimeBeforeScheduledRide(scheduled,order,false);
                }
                if(!acceptedRideOverlaps && hasTimeBeforeScheduled) mostSuitable.add(freeDriver);
            }
        }

        //PHASE 4: Filtering drivers based on distance (the closest one to order's departure is the most suitable one)
        if(mostSuitable.size() != 0) {
            Location departure = null;
            for(Route route : order.getLocations()) {
                departure = route.getDeparture(); break;
            }
            return this.getClosestDriver(mostSuitable,departure);
        }

        //PHASE 5: Filtering drivers that have ride in progress based on time(if they have a scheduled ride, they should have time for the ordered ride)
        //and if they have previously accepted ride, they should have time to finish it before ordered time
        for(Driver inRideDriver : inRide) {
            //He is in ride, active, and doesn't have scheduled ride
            boolean hasPreviouslyAcceptedRide = this.hasAcceptedRide(inRideDriver,order);
            boolean hasScheduledRide = this.hasScheduledRide(inRideDriver, now);
            if(!hasPreviouslyAcceptedRide && !hasScheduledRide) mostSuitable.add(inRideDriver);
            else {
                boolean acceptedRideOverlaps = false;
                boolean hasTimeBeforeScheduled = true;
                if(hasPreviouslyAcceptedRide) {
                    acceptedRideOverlaps = this.doesPreviouslyAcceptedRideOverlap(order,inRideDriver,true);
                }
                if(hasScheduledRide) {
                    Ride scheduled = this.rideRepository.findFirstByDriver_IdAndScheduledTimeIsAfter(inRideDriver.getId(), now).get();
                    hasTimeBeforeScheduled = this.hasTimeBeforeScheduledRide(scheduled,order,true);
                }
                if(!acceptedRideOverlaps && hasTimeBeforeScheduled) mostSuitable.add(inRideDriver);
            }
        }

        //PHASE 6: Filtering drivers based on finish time (the closest one to finish current order will be the most suitable)
        if(mostSuitable.size() != 0) {
            return this.getSoonestToFinishDriver(mostSuitable);
        }
        return null;
    }

    private boolean canFitInWorkingHours(Ride order, Driver driver, boolean isReservation) {
        long minutesWorked = this.workingHoursService.getDurationWorkedInPastDay(driver.getId()).toMinutes();
        if(isReservation) { //check if their working hours will not exceed 8 hours because of order
            double orderTravelTime = 0;
            for(Route route : order.getLocations()) {
                double distance = this.estimatesService.calculateDistance(route.getDeparture(),route.getDestination());
                orderTravelTime += this.estimatesService.getEstimatedTime(distance);
            }
            long fromNow = Duration.between(LocalDateTime.now(),order.getScheduledTime()).toMinutes();
            if(minutesWorked + fromNow >= 8 * 60) return false; // reservation is outside working hours limit
            if(minutesWorked + fromNow + orderTravelTime > 8*60) return false; //reservation duration exceeds 8 hours limit
        }
        else {if(minutesWorked >= 8*60) return false;} //8 hours in minutes
        return true;
    }

    private boolean isVehicleSuitable(Ride order, Vehicle driversVehicle) {
        if(driversVehicle.getVehicleType().getId().equals(order.getVehicleType().getId()) &&
                driversVehicle.getPassengerSeats() >= order.getPassengers().size()) {
            return (driversVehicle.getPetTransport() && driversVehicle.getBabyTransport()) ||
                    ((!driversVehicle.getBabyTransport() && !order.getBabyTransport()) &&
                            (!driversVehicle.getPetTransport() && !order.getPetTransport())
                    );
        }
        return false;
    }

    private boolean isInActiveRide(Driver driver) {
        Optional<Ride> active = this.rideRepository.findByDriver_IdAndStatus(driver.getId(),RideStatus.ACTIVE);
        return active.isPresent();
    }

    private boolean hasScheduledRide(Driver driver, LocalDateTime now) {
        Optional<Ride> scheduled = this.rideRepository.findFirstByDriver_IdAndScheduledTimeIsAfter(driver.getId(), now);
        if (scheduled.isEmpty()) return false;
        return scheduled.get().getStatus() != RideStatus.CANCELLED && scheduled.get().getStatus() != RideStatus.REJECTED;
    }

    private int getTotalTravelTimeMinutes(Ride order, Location start) {
        double totalTimeOfTravelForDriver = 0;
        Location departureOfOrder = null;
        for (Route route : order.getLocations()) {
            if (departureOfOrder == null) departureOfOrder = route.getDeparture(); //will change only first time
            double routeDistance = this.estimatesService.calculateDistance(route.getDeparture(), route.getDestination());
            totalTimeOfTravelForDriver += this.estimatesService.getEstimatedTime(routeDistance);
        }
        double distanceBetweenVehicleAndOrderDeparture = this.estimatesService.calculateDistance(start, departureOfOrder);
        totalTimeOfTravelForDriver += this.estimatesService.getEstimatedTime(distanceBetweenVehicleAndOrderDeparture);
        return (int)Math.round(totalTimeOfTravelForDriver);
    }

    private boolean hasTimeBeforeScheduledRide(Ride scheduled, Ride order, boolean isInActiveRide) {
        Location whereDriverShouldBe = null;
        if(isInActiveRide){ // the starting location will be where active ride finishes
            Ride active = this.rideRepository.findByDriver_IdAndStatus(scheduled.getDriver().getId(),RideStatus.ACTIVE).get();
            int i = active.getLocations().size() - 1;
            for(Route route : active.getLocations()){
                if(i-- == 0)  whereDriverShouldBe = route.getDestination();
                break;
            }
        }
        //the starting location will be current vehicle location, since it is not in ride
        else whereDriverShouldBe = scheduled.getDriver().getVehicle().getCurrentLocation();
        int totalTravelTimeMinutes = this.getTotalTravelTimeMinutes(order,whereDriverShouldBe);
        LocalDateTime orderEstimatedEndTime = order.getScheduledTime().plus(totalTravelTimeMinutes,ChronoUnit.MINUTES);
        return !orderEstimatedEndTime.isAfter(scheduled.getScheduledTime());
    }

    private boolean hasAcceptedRide(Driver driver,Ride order) {
        Optional<Ride> previouslyAccepted = this.rideRepository.findFirstByDriver_IdAndScheduledTimeIsBeforeOrderByScheduledTimeDesc(driver.getId(), order.getScheduledTime());
        if (previouslyAccepted.isEmpty()) return false;
        return previouslyAccepted.get().getStatus() != RideStatus.CANCELLED &&
                previouslyAccepted.get().getStatus() != RideStatus.REJECTED &&
                previouslyAccepted.get().getStatus() != RideStatus.FINISHED;
    }

    private boolean doesPreviouslyAcceptedRideOverlap(Ride order,Driver driver,boolean isInActiveRide){
        Location whereDriverShouldBe = null;
        int totalTravelTimeMinutes = 0;
        LocalDateTime activeRideEstimatedFinish = null;
        if(isInActiveRide){
            Ride active = this.rideRepository.findByDriver_IdAndStatus(driver.getId(),RideStatus.ACTIVE).get();
            int i = active.getLocations().size() - 1;
            for(Route route : active.getLocations()){
                if(i-- == 0)  whereDriverShouldBe = route.getDestination();
                break;
            }
            activeRideEstimatedFinish = active.getStartTime().plus(active.getEstimatedTimeInMinutes(),ChronoUnit.MINUTES);

        }
        else whereDriverShouldBe = driver.getVehicle().getCurrentLocation();
        Ride accepted =
                        this.rideRepository.findFirstByDriver_IdAndScheduledTimeIsBeforeOrderByScheduledTimeDesc(
                        driver.getId(), order.getScheduledTime()).get();

        totalTravelTimeMinutes += this.getTotalTravelTimeMinutes(accepted,whereDriverShouldBe);
        LocalDateTime totalEstimatedEndTime;
        if(activeRideEstimatedFinish != null) totalEstimatedEndTime = activeRideEstimatedFinish.plus(totalTravelTimeMinutes,ChronoUnit.MINUTES);
        else totalEstimatedEndTime = accepted.getScheduledTime().plus(totalTravelTimeMinutes,ChronoUnit.MINUTES);
        return totalEstimatedEndTime.isAfter(order.getScheduledTime());
    }

    private Driver getClosestDriver(List<Driver> freeDrivers, Location departure) {
        Driver closest = null;
        double closestDistance = -1;
        for (Driver driver : freeDrivers) {
            if (driver.getVehicle() == null) continue;
            Location currentDriverLocation = driver.getVehicle().getCurrentLocation();
            double distanceBetweenDriverAndDeparture = this.estimatesService.calculateDistance(currentDriverLocation, departure);
            if(closest == null) { //initialization
                closest = driver;
                closestDistance = distanceBetweenDriverAndDeparture;
                continue;
            }
            if (distanceBetweenDriverAndDeparture > closestDistance) continue;
            closest = driver;
            closestDistance = distanceBetweenDriverAndDeparture;
        }
        return closest;
    }

    private Driver getSoonestToFinishDriver(List<Driver> inRideDrivers) {
        Driver soonest = null;
        LocalDateTime soonestFinish = LocalDateTime.MIN;
        for (Driver driver : inRideDrivers) {
            Ride inProgress = this.rideRepository.findByDriver_IdAndStatus(driver.getId(),RideStatus.ACTIVE).get();
            LocalDateTime rideInProgressFinish = inProgress.getStartTime().plus(inProgress.getEstimatedTimeInMinutes(),ChronoUnit.MINUTES);
            if(soonest == null && soonestFinish.isEqual(LocalDateTime.MIN)) { // initialization
                soonest = driver;
                soonestFinish = rideInProgressFinish;
                continue;
            }
            if(rideInProgressFinish.isBefore(soonestFinish)) continue;
            soonest = driver;
            soonestFinish = rideInProgressFinish;
        }
        return soonest;
    }
}
