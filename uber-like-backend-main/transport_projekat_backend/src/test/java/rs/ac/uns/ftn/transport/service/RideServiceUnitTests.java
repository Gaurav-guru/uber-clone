package rs.ac.uns.ftn.transport.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.web.server.ResponseStatusException;
import rs.ac.uns.ftn.transport.dto.LocationDTO;
import rs.ac.uns.ftn.transport.dto.RouteDTO;
import rs.ac.uns.ftn.transport.dto.passenger.PassengerIdEmailDTO;
import rs.ac.uns.ftn.transport.dto.ride.RideCreationDTO;
import rs.ac.uns.ftn.transport.mapper.LocationDTOMapper;
import rs.ac.uns.ftn.transport.mapper.ride.RideCreationDTOMapper;
import rs.ac.uns.ftn.transport.model.Driver;
import rs.ac.uns.ftn.transport.model.Location;
import rs.ac.uns.ftn.transport.model.Ride;
import rs.ac.uns.ftn.transport.model.enumerations.RideStatus;
import rs.ac.uns.ftn.transport.repository.DriverRepository;
import rs.ac.uns.ftn.transport.repository.RideRepository;
import rs.ac.uns.ftn.transport.service.interfaces.IFindingDriverService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
public class RideServiceUnitTests {
    @Autowired
    RideServiceImpl rideService;
    @MockBean
    RideRepository rideRepository;
    @MockBean
    DriverRepository driverRepository;
    @MockBean
    IFindingDriverService findingDriverService;
    @MockBean
    EstimatesService estimatesService;
    @Autowired
    MessageSource messageSource;
    @Autowired
    RideCreationDTOMapper rideCreationDTOMapper;


    @Test
    @DisplayName("Should throw error when passenger tries to create ride but has one pending")
    public void shouldThrowErrorWhenCreatingRideIfPendingRideExists() {
        Ride mockRide = new Ride();
        Mockito.when(rideRepository.findByPassengers_IdAndStatus(1, RideStatus.PENDING)).thenReturn(Optional.of(mockRide));
        assertThrows(ResponseStatusException.class, () -> rideService.save(mockRide,false,1));
    }

    @Test
    @DisplayName("Should throw error when passenger tries to create ride but no driver is available")
    public void shouldThrowErrorWhenCreatingRideIfNoDriverAvailable() {
        Ride mockRide = new Ride();
        Mockito.when(rideRepository.findByPassengers_IdAndStatus(1,RideStatus.PENDING)).thenReturn(Optional.empty());
        Mockito.when(findingDriverService.findSuitableDriver(mockRide,false)).thenReturn(null);
        assertThrows(ResponseStatusException.class, () -> rideService.save(mockRide,false,1));
    }

    @Test
    @DisplayName("Should create ride if passenger doesn't have pending ride and drivers are available" )
    public void shouldSaveRide() {

        LocationDTO location = new LocationDTO("Bulevar oslobodjenja 46",45.267136,19.833549);
        Location locationPlain = LocationDTOMapper.fromDTOtoLocation(location);
        String vehicleType = "STANDARD";
        RouteDTO route = new RouteDTO(location,location);
        PassengerIdEmailDTO passenger = new PassengerIdEmailDTO(1,"passenger1@mail.com");
        Set<RouteDTO> locations = new HashSet<>();
        locations.add(route);
        Set<PassengerIdEmailDTO> passengers = new HashSet<>();
        passengers.add(passenger);
        RideCreationDTO rideOrder = new RideCreationDTO(locations,
                passengers,
                vehicleType,
                true,
                true,
                null);
        Ride expectedRide = RideCreationDTOMapper.fromDTOtoRide(rideOrder);
        Driver suitableDriver = new Driver();
        suitableDriver.setId(2);
        expectedRide.setDriver(suitableDriver);
        double distance = 1.0,estimatedTime = 5.0, estimatedPrice = 300.0;
        expectedRide.setStatus(RideStatus.PENDING);
        expectedRide.setEstimatedTimeInMinutes((int)estimatedTime);
        expectedRide.setTotalCost(estimatedPrice);
        Mockito.when(rideRepository.findByPassengers_IdAndStatus(1,RideStatus.PENDING)).thenReturn(Optional.empty());
        Mockito.when(findingDriverService.findSuitableDriver(expectedRide,false)).thenReturn(suitableDriver);
        Mockito.when(estimatesService.calculateDistance(locationPlain,locationPlain)).thenReturn(distance);
        Mockito.when(estimatesService.getEstimatedTime(distance)).thenReturn(estimatedTime);
        Mockito.when(estimatesService.getEstimatedPrice(expectedRide.getVehicleType(),distance)).thenReturn(estimatedPrice);
        Mockito.when(rideRepository.save(Mockito.any(Ride.class))).thenReturn(expectedRide);
        Ride actualRide = rideService.save(expectedRide,false,1);

        Assertions.assertThat(actualRide.getVehicleType().getName()).isEqualTo(expectedRide.getVehicleType().getName());
        Assertions.assertThat(actualRide.getDriver().getId()).isEqualTo(suitableDriver.getId());
        Assertions.assertThat(actualRide.getLocations().containsAll(expectedRide.getLocations()));
        Assertions.assertThat(actualRide.getPassengers().containsAll(expectedRide.getPassengers()));
        Assertions.assertThat(actualRide.getStatus()).isEqualTo(RideStatus.PENDING);
        Assertions.assertThat(actualRide.getBabyTransport().equals(expectedRide.getBabyTransport()));
        Assertions.assertThat(actualRide.getPetTransport().equals(expectedRide.getPetTransport()));
        Assertions.assertThat(actualRide.getEstimatedTimeInMinutes().equals(expectedRide.getEstimatedTimeInMinutes()));
        Assertions.assertThat(actualRide.getStartTime() == null);
        Assertions.assertThat(actualRide.getEndTime() == null);
        Assertions.assertThat(Duration.between(LocalDateTime.now(),actualRide.getScheduledTime()).toSeconds() < 1);
    }

    @Test
    @DisplayName("Should reserve ride for later if passenger doesn't have pending ride and drivers are available" )
    public void shouldSaveRideForReservation() {
        LocationDTO location = new LocationDTO("Bulevar oslobodjenja 46",45.267136,19.833549);
        Location locationPlain = LocationDTOMapper.fromDTOtoLocation(location);
        String vehicleType = "STANDARD";
        RouteDTO route = new RouteDTO(location,location);
        PassengerIdEmailDTO passenger = new PassengerIdEmailDTO(1,"passenger1@mail.com");
        Set<RouteDTO> locations = new HashSet<>();
        locations.add(route);
        Set<PassengerIdEmailDTO> passengers = new HashSet<>();
        passengers.add(passenger);
        RideCreationDTO rideOrder = new RideCreationDTO(locations,
                passengers,
                vehicleType,
                true,
                true,
                null);
        Ride expectedRide = RideCreationDTOMapper.fromDTOtoRide(rideOrder);
        Driver suitableDriver = new Driver();
        suitableDriver.setId(2);
        expectedRide.setDriver(suitableDriver);
        double distance = 1.0,estimatedTime = 5.0, estimatedPrice = 300.0;
        LocalDateTime scheduleTime = LocalDateTime.now().plus(1, ChronoUnit.HOURS);
        expectedRide.setStatus(RideStatus.PENDING);
        expectedRide.setEstimatedTimeInMinutes((int)estimatedTime);
        expectedRide.setTotalCost(estimatedPrice);
        expectedRide.setScheduledTime(scheduleTime);
        Mockito.when(rideRepository.findByPassengers_IdAndStatus(1,RideStatus.PENDING)).thenReturn(Optional.empty());
        Mockito.when(findingDriverService.findSuitableDriver(expectedRide,true)).thenReturn(suitableDriver);
        Mockito.when(estimatesService.calculateDistance(locationPlain,locationPlain)).thenReturn(distance);
        Mockito.when(estimatesService.getEstimatedTime(distance)).thenReturn(estimatedTime);
        Mockito.when(estimatesService.getEstimatedPrice(expectedRide.getVehicleType(),distance)).thenReturn(estimatedPrice);
        Mockito.when(rideRepository.save(Mockito.any(Ride.class))).thenReturn(expectedRide);
        Ride actualRide = rideService.save(expectedRide,true,1);

        Assertions.assertThat(actualRide.getVehicleType().getName()).isEqualTo(expectedRide.getVehicleType().getName());
        Assertions.assertThat(actualRide.getDriver().getId()).isEqualTo(suitableDriver.getId());
        Assertions.assertThat(actualRide.getLocations().containsAll(expectedRide.getLocations()));
        Assertions.assertThat(actualRide.getPassengers().containsAll(expectedRide.getPassengers()));
        Assertions.assertThat(actualRide.getStatus()).isEqualTo(RideStatus.PENDING);
        Assertions.assertThat(actualRide.getBabyTransport().equals(expectedRide.getBabyTransport()));
        Assertions.assertThat(actualRide.getPetTransport().equals(expectedRide.getPetTransport()));
        Assertions.assertThat(actualRide.getEstimatedTimeInMinutes().equals(expectedRide.getEstimatedTimeInMinutes()));
        Assertions.assertThat(actualRide.getStartTime() == null);
        Assertions.assertThat(actualRide.getEndTime() == null);
        Assertions.assertThat(actualRide.getScheduledTime().equals(expectedRide.getScheduledTime()));
    }

    @Test
    @DisplayName("Should throw error when passenger tries to reserve ride but schedule time is in past")
    public void shouldThrowErrorWhenReservingRideIfScheduleTimeInPast() {
        Ride mockRide = new Ride();
        mockRide.setScheduledTime(LocalDateTime.now().minus(1,ChronoUnit.HOURS));
        assertThrows(ResponseStatusException.class, () -> rideService.reserve(mockRide,1));

    }
    @Test
    @DisplayName("Should throw error when passenger tries to reserve ride but schedule time is more than 5 hours ahead")
    public void shouldThrowErrorWhenReservingRideIfScheduleTimeMoreThan5Hours() {
        Ride mockRide = new Ride();
        mockRide.setScheduledTime(LocalDateTime.now().plus(5 * 60 + 2,ChronoUnit.MINUTES));
        assertThrows(ResponseStatusException.class, () -> rideService.reserve(mockRide,1));
    }

    @Test
    @DisplayName("Should throw error when passenger tries to reserve ride but schedule time is less than 30 mins from start")
    public void shouldThrowErrorWhenReservingRideIfScheduleTimeLessThan30Mins() {
        Ride mockRide = new Ride();
        mockRide.setScheduledTime(LocalDateTime.now().plus(29,ChronoUnit.MINUTES));
        assertThrows(ResponseStatusException.class, () -> rideService.reserve(mockRide,1));
    }

    @Test
    @DisplayName("Should reserve ride if schedule time is correct")
    public void shouldReserveRide() {
        Ride mockRide = new Ride();
        mockRide.setScheduledTime(LocalDateTime.now().plus(60,ChronoUnit.MINUTES));
        Assertions.assertThatNoException().isThrownBy(() -> rideService.reserve(mockRide,1));
    }

    @Test
    @DisplayName("Should throw error when finding ride that does not exist")
    public void shouldThrowErrorIfRideDoesNotExist() {
        Mockito.when(rideRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class,() -> rideService.findOne(1));
    }

    @Test
    @DisplayName("Should return ride if it exists")
    public void shouldFindRideIfExists() {
        Ride expectedRide = new Ride();
        expectedRide.setId(1);
        Mockito.when(rideRepository.findById(1)).thenReturn(Optional.of(expectedRide));
        Ride actualRide = rideService.findOne(1);
        assertEquals(actualRide.getId(),expectedRide.getId());
    }

    @Test
    @DisplayName("Should throw error when finding an active ride for driver that does not exist")
    public void shouldThrowErrorIfActiveRideForDriverDoesNotExist() {
        Mockito.when(rideRepository.findByDriver_IdAndStatus(1,RideStatus.ACTIVE)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class,() -> rideService.findActiveForDriver(1));
    }

    @Test
    @DisplayName("Should return an active ride for driver if it exists")
    public void shouldFindActiveRideForDriverIfExists() {
        Ride expectedRide = new Ride();
        expectedRide.setId(1);
        Driver selectedDriver = new Driver();
        selectedDriver.setId(1);
        expectedRide.setDriver(selectedDriver);
        Mockito.when(rideRepository.findByDriver_IdAndStatus(1,RideStatus.ACTIVE)).thenReturn(Optional.of(expectedRide));
        Ride actualRide = rideService.findActiveForDriver(1);
        assertEquals(actualRide.getId(),expectedRide.getId());
        assertEquals(actualRide.getDriver().getId(),selectedDriver.getId());
    }


    @Test
    @DisplayName("Should throw error when finding an active ride for passenger that does not exist")
    public void shouldThrowErrorIfActiveRideForPassengerDoesNotExist() {
        Mockito.when(rideRepository.findByPassengers_IdAndStatus(1,RideStatus.ACTIVE)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class,() -> rideService.findActiveForPassenger(1));
    }

    @Test
    @DisplayName("Should return an active ride for passenger if it exists")
    public void shouldFindActiveRideForPassengerIfExists() {
        Ride expectedRide = new Ride();
        expectedRide.setId(1);
        Driver selectedDriver = new Driver();
        selectedDriver.setId(1);
        expectedRide.setDriver(selectedDriver);
        Mockito.when(rideRepository.findByPassengers_IdAndStatus(1,RideStatus.ACTIVE)).thenReturn(Optional.of(expectedRide));
        Ride actualRide = rideService.findActiveForPassenger(1);
        assertEquals(actualRide.getId(),expectedRide.getId());
        assertEquals(actualRide.getDriver().getId(),selectedDriver.getId());
    }
}
