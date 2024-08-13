package rs.ac.uns.ftn.transport.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import rs.ac.uns.ftn.transport.model.*;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DataJpaTest
@ActiveProfiles("test")
public class RideRepositoryUnitTests {

    @Autowired
    private RideRepository rideRepository;

    @Test
    @DisplayName("Should save ride based on passed ride object")
    public void shouldSaveRide() {
        Location location = new Location();
        location.setAddress("Bulevar oslobodjenja 46");
        location.setLatitude(45.0);
        location.setLongitude(19.4);
        Route route = new Route(location,location);
        Passenger passenger = new Passenger();
        passenger.setId(1);
        passenger.setEmail("passenger1@mail.com");
        Set<Route> locations = new HashSet<>();
        locations.add(route);
        Set<Passenger> passengers = new HashSet<>();
        passengers.add(passenger);
        Ride rideOrder = new Ride();
        rideOrder.setPassengers(passengers);
        rideOrder.setLocations(locations);
        rideOrder.setBabyTransport(true);
        rideOrder.setPetTransport(true);
        VehicleType vehicleType = new VehicleType();
        vehicleType.setName("STANDARD");
        rideOrder.setEstimatedTimeInMinutes(5);
        rideOrder.setTotalCost(300.0);
        Driver suitableDriver = new Driver();
        suitableDriver.setEmail("driver1@mail.com");
        rideOrder.setDriver(suitableDriver);
        rideOrder.setVehicleType(vehicleType);
        Ride retrievedRide = rideRepository.save(rideOrder);
        assertThat(rideOrder).usingRecursiveComparison().ignoringFields("id").isEqualTo(retrievedRide);
    }

    @Test
    @DisplayName("Should get ride by id")
    @Sql("classpath:test-data-insert-ride.sql")
    public void shouldGetRideById(){
        Optional<Ride> retrievedRide = rideRepository.findById(1);
        assertThat(retrievedRide).isNotEmpty();
        assertThat(retrievedRide.get().getId()).isEqualTo(1);
        assertThat(retrievedRide.get().getTotalCost()).isEqualTo(1200);
    }

    @Test
    @DisplayName("Shouldn't get ride by id if it doesn't exist")
    @Sql("classpath:test-data-insert-ride.sql")
    public void shouldNotGetRideByIdIfDoesntExist(){
        Optional<Ride> retrievedRide = rideRepository.findById(2);
        assertThat(retrievedRide).isEmpty();
    }

    @Test
    @DisplayName("Should count rides by passenger id and ride date")
    @Sql("classpath:test-data-reports.sql")
    public void shouldCountRidesByPassengerIdAndRideDate(){
        int count = rideRepository.countRidesByPassengerIdAndRideDate(1,
                LocalDateTime.of(2023, Month.JANUARY, 30, 0, 0),
                LocalDateTime.of(2023, Month.JANUARY, 31, 0, 0));

        assertEquals(3, count);

        count = rideRepository.countRidesByPassengerIdAndRideDate(1,
                LocalDateTime.of(2023, Month.JANUARY, 31, 0, 0),
                LocalDateTime.of(2023, Month.FEBRUARY, 1, 0, 0));

        assertEquals(0, count);

        count = rideRepository.countRidesByPassengerIdAndRideDate(1,
                LocalDateTime.of(2023, Month.FEBRUARY, 1, 0, 0),
                LocalDateTime.of(2023, Month.FEBRUARY, 2, 0, 0));

        assertEquals(1, count);

        count = rideRepository.countRidesByPassengerIdAndRideDate(1,
                LocalDateTime.of(2023, Month.FEBRUARY, 2, 0, 0),
                LocalDateTime.of(2023, Month.FEBRUARY, 3, 0, 0));

        assertEquals(3, count);
    }

    @Test
    @DisplayName("Should sum distance by passenger id and ride date")
    @Sql("classpath:test-data-reports.sql")
    public void shouldSumDistanceByPassengerIdAndRideDate(){
        double distance = rideRepository.sumDistanceByPassengerIdAndRideDate(1,
                LocalDateTime.of(2023, Month.JANUARY, 30, 0, 0),
                LocalDateTime.of(2023, Month.JANUARY, 31, 0, 0));

        assertEquals(35, distance);

        assertNull(rideRepository.sumDistanceByPassengerIdAndRideDate(1,
                LocalDateTime.of(2023, Month.JANUARY, 31, 0, 0),
                LocalDateTime.of(2023, Month.FEBRUARY, 1, 0, 0)));

        distance = rideRepository.sumDistanceByPassengerIdAndRideDate(1,
                LocalDateTime.of(2023, Month.FEBRUARY, 1, 0, 0),
                LocalDateTime.of(2023, Month.FEBRUARY, 2, 0, 0));

        assertEquals(5, distance);

        distance = rideRepository.sumDistanceByPassengerIdAndRideDate(1,
                LocalDateTime.of(2023, Month.FEBRUARY, 2, 0, 0),
                LocalDateTime.of(2023, Month.FEBRUARY, 3, 0, 0));

        assertEquals(35, distance);
    }

    @Test
    @DisplayName("Should sum price by passenger id and ride date")
    @Sql("classpath:test-data-reports.sql")
    public void shouldSumPriceByPassengerIdAndRideDate(){
        double price = rideRepository.sumPriceByPassengerIdAndRideDate(1,
                LocalDateTime.of(2023, Month.JANUARY, 30, 0, 0),
                LocalDateTime.of(2023, Month.JANUARY, 31, 0, 0));

        assertEquals(3600, price);

        assertNull(rideRepository.sumPriceByPassengerIdAndRideDate(1,
                LocalDateTime.of(2023, Month.JANUARY, 31, 0, 0),
                LocalDateTime.of(2023, Month.FEBRUARY, 1, 0, 0)));

        price = rideRepository.sumPriceByPassengerIdAndRideDate(1,
                LocalDateTime.of(2023, Month.FEBRUARY, 1, 0, 0),
                LocalDateTime.of(2023, Month.FEBRUARY, 2, 0, 0));

        assertEquals(2000, price);

        price = rideRepository.sumPriceByPassengerIdAndRideDate(1,
                LocalDateTime.of(2023, Month.FEBRUARY, 2, 0, 0),
                LocalDateTime.of(2023, Month.FEBRUARY, 3, 0, 0));

        assertEquals(3000, price);
    }
}
