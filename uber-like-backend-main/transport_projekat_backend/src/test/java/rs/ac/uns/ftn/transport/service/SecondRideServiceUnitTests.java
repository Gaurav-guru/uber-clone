package rs.ac.uns.ftn.transport.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;
import rs.ac.uns.ftn.transport.model.Ride;
import rs.ac.uns.ftn.transport.model.enumerations.RideStatus;
import rs.ac.uns.ftn.transport.repository.RideRepository;
import rs.ac.uns.ftn.transport.service.interfaces.IRideService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
public class SecondRideServiceUnitTests {
    @Autowired
    IRideService rideService;
    @MockBean
    RideRepository rideRepository;

    @Test
    @DisplayName("Should accept ride if it exists and is pending")
    public void shouldAcceptRideIfItExistsAndIsPending() {
        int rideId = 1;
        Ride foundRide = new Ride();
        foundRide.setStatus(RideStatus.PENDING);
        when(rideRepository.findById(rideId)).thenReturn(Optional.of(foundRide));

        Ride acceptedRide = rideService.acceptRide(rideId);

        assertEquals(acceptedRide.getStatus(), RideStatus.ACCEPTED);
    }

    @Test
    @DisplayName("Should not accept ride if it does not exist")
    public void shouldNotAcceptRideIfItDoesNotExist() {
        int rideId = 1;
        when(rideRepository.findById(rideId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> {
            rideService.acceptRide(rideId);
        });
    }

    @Test
    @DisplayName("Should not accept ride if it is not pending")
    public void shouldNotAcceptRideIfItIsNotPending() {
        int rideId = 1;
        Ride foundRide = new Ride();
        foundRide.setStatus(RideStatus.ACCEPTED);
        when(rideRepository.findById(rideId)).thenReturn(Optional.of(foundRide));

        assertThrows(ResponseStatusException.class, () -> {
            rideService.acceptRide(rideId);
        });
    }

    @Test
    @DisplayName("Should start ride if it exists and is accepted")
    public void shouldStartRideIfItExistsAndIsAccepted() {
        int rideId = 1;
        Ride foundRide = new Ride();
        foundRide.setStatus(RideStatus.ACCEPTED);
        when(rideRepository.findById(rideId)).thenReturn(Optional.of(foundRide));

        Ride startedRide = rideService.startRide(rideId);

        assertEquals(startedRide.getStatus(), RideStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should not start ride if it does not exist")
    public void shouldNotStartRideIfItDoesNotExist() {
        int rideId = 1;
        when(rideRepository.findById(rideId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> {
            rideService.startRide(rideId);
        });
    }

    @Test
    @DisplayName("Should not start ride if it is not accepted")
    public void shouldNotStartRideIfItIsNotAccepted() {
        int rideId = 1;
        Ride foundRide = new Ride();
        foundRide.setStatus(RideStatus.PENDING);
        when(rideRepository.findById(rideId)).thenReturn(Optional.of(foundRide));

        assertThrows(ResponseStatusException.class, () -> {
            rideService.startRide(rideId);
        });
    }

    @Test
    @DisplayName("Should find all rides by driver id")
    public void shouldFindAllRidesByDriverId() {
        int driverId = 1;
        Pageable pageable = PageRequest.of(0, 10);

        // when finding all rides by driver id from repository, return page with 2 rides
        when(rideRepository.findAllByDriver_Id(driverId, pageable)).thenReturn(new PageImpl<>(Arrays.asList(new Ride(), new Ride())));

        // assert that page with 2 rides is returned from service
        assertEquals(rideService.findAllByDriver_Id(driverId, pageable).getNumberOfElements(), 2);
    }

    @Test
    @DisplayName("Should find all rides by driver id and start time is after given time and end time is before it")
    public void shouldFindAllRidesByDriverIdAndStartTimeIsAfterGivenTimeAndEndTimeIsBeforeIt() {
        int driverId = 1;
        Pageable pageable = PageRequest.of(0, 10);
        LocalDateTime startTime = LocalDateTime.of(2020, 1, 1, 0, 0, 0);
        LocalDateTime endTime = LocalDateTime.now();

        when(rideRepository.findAllByDriver_IdAndStartTimeIsAfterAndEndTimeIsBefore(driverId, startTime, endTime, pageable))
                .thenReturn(new PageImpl<>(Arrays.asList(new Ride(), new Ride())));

        assertEquals(rideService.findAllByDriver_IdAndStartTimeIsAfterAndEndTimeIsBefore(driverId, startTime, endTime, pageable).getNumberOfElements(), 2);
    }

    @Test
    @DisplayName("Should find all rides by driver id and start time is after given time")
    public void shouldFindAllRidesByDriverIdAndStartTimeIsAfterGivenTime() {
        int driverId = 1;
        Pageable pageable = PageRequest.of(0, 10);
        LocalDateTime startTime = LocalDateTime.of(2020, 1, 1, 0, 0, 0);

        when(rideRepository.findAllByDriver_IdAndStartTimeIsAfter(driverId, startTime, pageable))
                .thenReturn(new PageImpl<>(Arrays.asList(new Ride(), new Ride())));

        assertEquals(rideService.findAllByDriver_IdAndStartTimeIsAfter(driverId, startTime, pageable).getNumberOfElements(), 2);
    }

    @Test
    @DisplayName("Should find all rides by driver id and end time is before given time")
    public void shouldFindAllRidesByDriverIdAndEndTimeIsBeforeGivenTime() {
        int driverId = 1;
        Pageable pageable = PageRequest.of(0, 10);
        LocalDateTime endTime = LocalDateTime.now();

        when(rideRepository.findAllByDriver_IdAndEndTimeIsBefore(driverId, endTime, pageable))
                .thenReturn(new PageImpl<>(Arrays.asList(new Ride(), new Ride())));

        assertEquals(rideService.findAllByDriver_IdAndEndTimeIsBefore(driverId, endTime, pageable).getNumberOfElements(), 2);
    }
}
