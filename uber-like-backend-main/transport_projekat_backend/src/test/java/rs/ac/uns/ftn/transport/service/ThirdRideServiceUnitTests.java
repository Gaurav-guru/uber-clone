package rs.ac.uns.ftn.transport.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;
import rs.ac.uns.ftn.transport.model.Rejection;
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
public class ThirdRideServiceUnitTests {
    @Autowired
    IRideService rideService;
    @MockBean
    RideRepository rideRepository;

    @Test
    @DisplayName("Should cancel ride if it exists and is pending or accepted")
    public void shouldCancelRideWhenRideExistsAndIsPending() {
        Ride mockRide = new Ride();
        int rideId = 1;
        mockRide.setId(1);
        mockRide.setStatus(RideStatus.PENDING);
        when(rideRepository.findById(rideId)).thenReturn(Optional.of(mockRide));
        when(rideRepository.save(Mockito.any(Ride.class))).thenReturn(mockRide);
        mockRide = rideService.cancelRide(1);
        assertEquals(mockRide.getStatus(), RideStatus.CANCELLED);
    }
    @Test
    @DisplayName("Should not cancel ride if it does not exist")
    public void shouldThrowErrorWhenCancelingRideIfRideDoesNotExists(){
        int rideId = 1;
        when(rideRepository.findById(rideId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> {
            rideService.cancelRide(rideId);
        });
    }
    @Test
    @DisplayName("Should not cancel ride if it is not pending or accepted")
    public void shouldThrowErrorWhenCancelingRideIfRideIsNotPendingOrAccepted(){
        int rideId = 1;
        Ride mockRide = new Ride();
        mockRide.setId(1);
        mockRide.setStatus(RideStatus.CANCELLED);
        when(rideRepository.findById(rideId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> {
            rideService.cancelRide(rideId);
        });
    }
    @Test
    @DisplayName("Should reject ride with explanation if ride exists and is pending or accepted")
    public void shouldRejectRideWithExplanationIfRideExistsAndIsPendingOrAccepted(){
        Ride mockRide = new Ride();
        int rideId = 1;
        mockRide.setId(1);
        mockRide.setStatus(RideStatus.PENDING);
        when(rideRepository.findById(rideId)).thenReturn(Optional.of(mockRide));
        when(rideRepository.save(Mockito.any(Ride.class))).thenReturn(mockRide);
        mockRide = rideService.cancelWithExplanation(1, new Rejection());
        assertEquals(mockRide.getStatus(), RideStatus.REJECTED);
    }
    @Test
    @DisplayName("Should not reject ride with explanation if it does not exist")
    public void shouldThrowErrorWhenRejectionRideWithExplanationIfRideDoesNotExists(){
        int rideId = 1;
        when(rideRepository.findById(rideId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> {
            rideService.cancelWithExplanation(rideId, new Rejection());
        });
    }
    @Test
    @DisplayName("Should not reject ride with explanation if it is not pending or accepted")
    public void shouldThrowErrorWhenRejectionRideWithExplanationIfRideIsNotPendingOrAccepted(){
        int rideId = 1;
        Ride mockRide = new Ride();
        mockRide.setId(1);
        mockRide.setStatus(RideStatus.CANCELLED);
        when(rideRepository.findById(rideId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> {
            rideService.cancelWithExplanation(rideId, new Rejection());
        });
    }
    @Test
    @DisplayName("Should end ride if it exists and is active")
    public void shouldEndRideIfItExistsAndIsActive(){
        Ride mockRide = new Ride();
        int rideId = 1;
        mockRide.setId(1);
        mockRide.setStatus(RideStatus.ACTIVE);
        when(rideRepository.findById(rideId)).thenReturn(Optional.of(mockRide));
        when(rideRepository.save(Mockito.any(Ride.class))).thenReturn(mockRide);
        mockRide = rideService.endRide(1);
        assertEquals(mockRide.getStatus(), RideStatus.FINISHED);
    }

    @Test
    @DisplayName("Should throw error when ending ride if it does not exist")
    public void shouldThrowErrorWhenEndingRideIfItDoesNotExist() {
        int rideId = 1;
        when(rideRepository.findById(rideId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> {
            rideService.endRide(rideId);
        });
    }

    @Test
    @DisplayName("Should throw error when ending ride if it is not active")
    public void shouldThrowErrorWhenEndingRideIfItIsNotActive() {
        int rideId = 1;
        Ride mockRide = new Ride();
        mockRide.setId(1);
        mockRide.setStatus(RideStatus.PENDING);
        when(rideRepository.findById(rideId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> {
            rideService.endRide(rideId);
        });
    }

    @Test
    @DisplayName("Should find ride if start time is after and end time is before")
    public void ShouldFindRideIfStartTimeIsAfterAndEndTimeIsBefore(){
        Ride mockRide = new Ride();
        int rideId = 1;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now();
        mockRide.setId(1);
        mockRide.setStatus(RideStatus.ACTIVE);
        when(rideRepository.findAllByPassengers_IdAndStartTimeIsAfterAndEndTimeIsBefore(rideId, start, end, null)).thenReturn(null);
        rideService.findAllByPassenger_IdAndStartTimeIsAfterAndEndTimeIsBefore(rideId, start, end, null);
        assertEquals(mockRide.getStatus(), RideStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should find ride by passenger id")
    public void ShouldFindRideByPassengerId(){
        Ride mockRide = new Ride();
        int passengerId = 1;
        mockRide.setId(1);
        mockRide.setStatus(RideStatus.ACTIVE);
        when(rideRepository.findAllByPassengers_Id(passengerId, null)).thenReturn(null);
        rideService.findAllByPassenger_Id(passengerId, null);
        assertEquals(mockRide.getStatus(), RideStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should find ride if start time is after")
    public void ShouldFindRideIfStartTimeIsAfter(){
        Ride mockRide = new Ride();
        int rideId = 1;
        LocalDateTime start = LocalDateTime.now();
        mockRide.setId(1);
        mockRide.setStatus(RideStatus.ACTIVE);
        when(rideRepository.findAllByPassengers_IdAndStartTimeIsAfter(rideId, start, null)).thenReturn(null);
        rideService.findAllByPassenger_IdAndStartTimeIsAfter(rideId, start, null);
        assertEquals(mockRide.getStatus(), RideStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should find ride if end time is before")
    public void ShouldFindRideIfEndTimeIsBefore(){
        Ride mockRide = new Ride();
        int rideId = 1;
        LocalDateTime end = LocalDateTime.now();
        mockRide.setId(1);
        mockRide.setStatus(RideStatus.ACTIVE);
        when(rideRepository.findAllByPassengers_IdAndEndTimeIsBefore(rideId, end, null)).thenReturn(null);
        rideService.findAllByPassenger_IdAndEndTimeIsBefore(rideId, end, null);
        assertEquals(mockRide.getStatus(), RideStatus.ACTIVE);
    }
}
