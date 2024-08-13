package rs.ac.uns.ftn.transport.controller;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import rs.ac.uns.ftn.transport.dto.LocationDTO;
import rs.ac.uns.ftn.transport.dto.RouteDTO;
import rs.ac.uns.ftn.transport.dto.TokenDTO;
import rs.ac.uns.ftn.transport.dto.User.LoginDTO;
import rs.ac.uns.ftn.transport.dto.panic.ExtendedPanicDTO;
import rs.ac.uns.ftn.transport.dto.panic.PanicReasonDTO;
import rs.ac.uns.ftn.transport.dto.passenger.PassengerIdEmailDTO;
import rs.ac.uns.ftn.transport.dto.passenger.PassengerWithoutIdPasswordDTO;
import rs.ac.uns.ftn.transport.dto.ride.FavoriteRideDTO;
import rs.ac.uns.ftn.transport.dto.ride.RideCreatedDTO;
import rs.ac.uns.ftn.transport.dto.ride.RideCreationDTO;
import rs.ac.uns.ftn.transport.model.ResponseMessage;
import rs.ac.uns.ftn.transport.model.enumerations.RideStatus;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SecondRideControllerIntegrationTests {
    @Autowired
    private TestRestTemplate restTemplatePlain;
    @Autowired
    private MessageSource messageSource;
    private TestRestTemplate restTemplatePassenger, restTemplatePassengerSecond, restTemplateDriver, restTemplateAdmin;
    private String passengerJwtToken, passengerJwtTokenSecond, driverJwtToken, adminJwtToken;

    private final String BASE_URL_PATH = "http://localhost:8080/api";
    private final String RIDE_URL_PATH = BASE_URL_PATH + "/ride";

    private void instantiatePassengerRestTemplate() {
        RestTemplateBuilder builder = new RestTemplateBuilder(rt -> rt.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + this.passengerJwtToken);
            return execution.execute(request, body);
        }));
        this.restTemplatePassenger = new TestRestTemplate(builder);
        RestTemplateBuilder builderSecond = new RestTemplateBuilder(rt -> rt.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + this.passengerJwtTokenSecond);
            return execution.execute(request, body);
        }));
        this.restTemplatePassengerSecond = new TestRestTemplate(builderSecond);
    }

    private void instantiateDriverRestTemplate() {
        RestTemplateBuilder builder = new RestTemplateBuilder(rt -> rt.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + this.driverJwtToken);
            return execution.execute(request, body);
        }));
        this.restTemplateDriver = new TestRestTemplate(builder);
    }

    private void instantiateAdminRestTemplate() {
        RestTemplateBuilder builder = new RestTemplateBuilder(rt -> rt.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + this.adminJwtToken);
            return execution.execute(request, body);
        }));
        this.restTemplateAdmin = new TestRestTemplate(builder);
    }

    private void instantiateAllJwtTokens() {

        HttpEntity<LoginDTO> driverLoginCredentials = new HttpEntity<>(new LoginDTO("driver1@mail.com", "Test1Test"));
        ResponseEntity<TokenDTO> loginDriver = this.restTemplatePlain.exchange("/api/user/login",
                HttpMethod.POST,
                driverLoginCredentials,
                TokenDTO.class);
        this.driverJwtToken = loginDriver.getBody().getAccessToken();

        HttpEntity<LoginDTO> passengerLoginCredentials = new HttpEntity<>(new LoginDTO("passenger1@mail.com", "Test1Test"));
        ResponseEntity<TokenDTO> loginPassenger = restTemplatePlain.exchange("/api/user/login",
                HttpMethod.POST,
                passengerLoginCredentials,
                TokenDTO.class);
        this.passengerJwtToken = loginPassenger.getBody().getAccessToken();

        passengerLoginCredentials = new HttpEntity<>(new LoginDTO("passenger2@mail.com", "Test1Test"));
        ResponseEntity<TokenDTO> loginPassengerSecond = restTemplatePlain.exchange("/api/user/login",
                HttpMethod.POST,
                passengerLoginCredentials,
                TokenDTO.class);
        this.passengerJwtTokenSecond = loginPassengerSecond.getBody().getAccessToken();

        HttpEntity<LoginDTO> adminLoginCredentials = new HttpEntity<>(new LoginDTO("admin1@mail.com", "Test1Test"));
        ResponseEntity<TokenDTO> loginAdmin = this.restTemplatePlain.exchange("/api/user/login",
                HttpMethod.POST,
                adminLoginCredentials,
                TokenDTO.class);
        this.adminJwtToken = loginAdmin.getBody().getAccessToken();
    }

    @BeforeAll
    public void setupTemplates() {
        instantiateAllJwtTokens();
        instantiatePassengerRestTemplate();
        instantiateDriverRestTemplate();
        instantiateAdminRestTemplate();
    }

    @Test
    @DisplayName("Should get all passengers of a ride if it exists")
    @Order(1)
    public void shouldGetAllPassengersOfRideIfItExists() {
        LocationDTO location = new LocationDTO("Bulevar oslobodjenja 46", 45.0, 19.833549);
        RouteDTO route = new RouteDTO(location, location);

        PassengerIdEmailDTO passenger = new PassengerIdEmailDTO(1, "passenger1@mail.com");
        PassengerIdEmailDTO passengerSecond = new PassengerIdEmailDTO(2, "passenger2@mail.com");

        Set<RouteDTO> locations = new HashSet<>(Set.of(route));
        Set<PassengerIdEmailDTO> passengers = new HashSet<>(Set.of(passenger, passengerSecond));

        RideCreationDTO rideOrder = new RideCreationDTO(locations,
                passengers,
                "STANDARD",
                true,
                true,
                null);
        this.restTemplatePassenger.exchange(this.BASE_URL_PATH + "/ride", HttpMethod.POST, new HttpEntity<>(rideOrder), String.class);

        ResponseEntity<PassengerWithoutIdPasswordDTO[]> response = this.restTemplateDriver.getForEntity(
                RIDE_URL_PATH + "/1/passengers",
                PassengerWithoutIdPasswordDTO[].class);

        PassengerWithoutIdPasswordDTO[] passengersResponse = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(passengersResponse);
        assertEquals(2, passengersResponse.length);

        // assert that both of the passengers are in the response
        assertTrue(Arrays.stream(passengersResponse).anyMatch(p -> p.getEmail().equals(passenger.getEmail())));
        assertTrue(Arrays.stream(passengersResponse).anyMatch(p -> p.getEmail().equals(passengerSecond.getEmail())));
    }

    @Test
    @DisplayName("Should not get passengers if ride does not exist")
    @Order(2)
    public void shouldNotGetPassengersIfRideDoesNotExist() {
        ResponseEntity<PassengerWithoutIdPasswordDTO[]> response = this.restTemplateDriver.getForEntity(
                RIDE_URL_PATH + "/2/passengers",
                PassengerWithoutIdPasswordDTO[].class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Should not get passengers if user is unauthorized")
    @Order(3)
    public void shouldNotGetPassengersIfUserIsUnauthorized() {
        ResponseEntity<PassengerWithoutIdPasswordDTO> response = this.restTemplatePlain.getForEntity(
                RIDE_URL_PATH + "/1/passengers",
                PassengerWithoutIdPasswordDTO.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("Should not get passengers if user is passenger")
    @Order(4)
    public void shouldNotGetPassengersIfUserIsPassenger() {
        ResponseEntity<PassengerWithoutIdPasswordDTO> response = this.restTemplatePassenger.getForEntity(
                RIDE_URL_PATH + "/1/passengers",
                PassengerWithoutIdPasswordDTO.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @DisplayName("Should not get passengers if user is admin")
    @Order(5)
    public void shouldNotGetPassengersIfUserIsAdmin() {
        ResponseEntity<PassengerWithoutIdPasswordDTO> response = this.restTemplateAdmin.getForEntity(
                RIDE_URL_PATH + "/1/passengers",
                PassengerWithoutIdPasswordDTO.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @DisplayName("Should create panic if user is passenger while ride and reason are valid")
    @Order(6)
    public void shouldCreatePanicIfUserIsPassengerWhileRideAndReasonAreValid() {
        PanicReasonDTO panicReasonDTO = new PanicReasonDTO("I am in danger");

        ResponseEntity<ExtendedPanicDTO> response = this.restTemplatePassenger.exchange(RIDE_URL_PATH + "/1/panic",
                HttpMethod.PUT,
                new HttpEntity<>(panicReasonDTO),
                ExtendedPanicDTO.class);

        ExtendedPanicDTO panicDTO = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(panicDTO);
        assertEquals(panicReasonDTO.getReason(), panicDTO.getReason());
        assertEquals(panicDTO.getRide().getId(), 1);
        assertEquals(panicDTO.getUser().getId(), 1);
    }

    @Test
    @DisplayName("Should create panic if user is driver while ride and reason are valid")
    @Order(7)
    public void shouldCreatePanicIfUserIsDriverWhileRideAndReasonAreValid() {
        PanicReasonDTO panicReasonDTO = new PanicReasonDTO("I am in danger");

        ResponseEntity<ExtendedPanicDTO> response = this.restTemplateDriver.exchange(RIDE_URL_PATH + "/1/panic",
                HttpMethod.PUT,
                new HttpEntity<>(panicReasonDTO),
                ExtendedPanicDTO.class);

        ExtendedPanicDTO panicDTO = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(panicDTO);
        assertEquals(panicReasonDTO.getReason(), panicDTO.getReason());
        assertEquals(panicDTO.getRide().getId(), 1);
        assertEquals(panicDTO.getUser().getId(), 3);
    }

    @Test
    @DisplayName("Should not create panic if ride does not exist")
    @Order(8)
    public void shouldNotCreatePanicIfRideDoesNotExist() {
        PanicReasonDTO panicReasonDTO = new PanicReasonDTO("I am in danger");

        ResponseEntity<String> response = this.restTemplatePassenger.exchange(RIDE_URL_PATH + "/2/panic",
                HttpMethod.PUT,
                new HttpEntity<>(panicReasonDTO),
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(messageSource.getMessage("ride.notFound", null, Locale.getDefault()), response.getBody());
    }

    @Test
    @DisplayName("Should not create panic if reason is empty")
    @Order(9)
    public void shouldNotCreatePanicIfReasonIsEmpty() {
        PanicReasonDTO panicReasonDTO = new PanicReasonDTO("");

        ResponseEntity<String> response = this.restTemplatePassenger.exchange(RIDE_URL_PATH + "/1/panic",
                HttpMethod.PUT,
                new HttpEntity<>(panicReasonDTO),
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should not create panic if reason is null")
    @Order(10)
    public void shouldNotCreatePanicIfReasonIsNull() {
        PanicReasonDTO panicReasonDTO = new PanicReasonDTO(null);

        ResponseEntity<String> response = this.restTemplatePassenger.exchange(RIDE_URL_PATH + "/1/panic",
                HttpMethod.PUT,
                new HttpEntity<>(panicReasonDTO),
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should not create panic if reason is too long")
    @Order(11)
    public void shouldNotCreatePanicIfReasonIsTooLong() {
        PanicReasonDTO panicReasonDTO = new PanicReasonDTO("I am in danger".repeat(50));

        ResponseEntity<String> response = this.restTemplatePassenger.exchange(RIDE_URL_PATH + "/1/panic",
                HttpMethod.PUT,
                new HttpEntity<>(panicReasonDTO),
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should not create panic if user is unauthorized")
    @Order(12)
    public void shouldNotCreatePanicIfUserIsUnauthorized() {
        PanicReasonDTO panicReasonDTO = new PanicReasonDTO("I am in danger");

        ResponseEntity<String> response = this.restTemplatePlain.exchange(RIDE_URL_PATH + "/1/panic",
                HttpMethod.PUT,
                new HttpEntity<>(panicReasonDTO),
                String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("Should not create panic if user is admin")
    @Order(13)
    public void shouldNotCreatePanicIfUserIsAdmin() {
        PanicReasonDTO panicReasonDTO = new PanicReasonDTO("I am in danger");

        ResponseEntity<String> response = this.restTemplateAdmin.exchange(RIDE_URL_PATH + "/1/panic",
                HttpMethod.PUT,
                new HttpEntity<>(panicReasonDTO),
                String.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @DisplayName("Should not start ride if it's not accepted")
    @Order(14)
    public void shouldNotStartRideIfItIsNotAccepted() {
        ResponseEntity<ResponseMessage> response = this.restTemplateDriver.exchange(RIDE_URL_PATH + "/1/start",
                HttpMethod.PUT,
                null,
                ResponseMessage.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(messageSource.getMessage("starting.invalidStatus", null, Locale.getDefault()),
                response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should start ride if it's accepted")
    @Order(15)
    public void shouldStartRideIfItIsAccepted() {
        this.restTemplateDriver.exchange(RIDE_URL_PATH + "/1/accept",
                HttpMethod.PUT,
                null,
                String.class);

        ResponseEntity<RideCreatedDTO> response = this.restTemplateDriver.exchange(RIDE_URL_PATH + "/1/start",
                HttpMethod.PUT,
                null,
                RideCreatedDTO.class);

        RideCreatedDTO ride = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(ride);
        assertEquals(ride.getStatus(), RideStatus.ACTIVE);
        assertEquals(ride.getId(), 1);
    }

    @Test
    @DisplayName("Should not start ride if it doesn't exist")
    @Order(16)
    public void shouldNotStartRideIfItDoesNotExist() {
        ResponseEntity<String> response = this.restTemplateDriver.exchange(RIDE_URL_PATH + "/2/start",
                HttpMethod.PUT,
                null,
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(messageSource.getMessage("ride.notFound", null, Locale.getDefault()), response.getBody());
    }

    @Test
    @DisplayName("Should not start ride if user is unauthorized")
    @Order(17)
    public void shouldNotStartRideIfUserIsUnauthorized() {
        ResponseEntity<String> response = this.restTemplatePlain.exchange(RIDE_URL_PATH + "/1/start",
                HttpMethod.PUT,
                null,
                String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("Should not start ride if user is admin")
    @Order(18)
    public void shouldNotStartRideIfUserIsAdmin() {
        ResponseEntity<String> response = this.restTemplateAdmin.exchange(RIDE_URL_PATH + "/1/start",
                HttpMethod.PUT,
                null,
                String.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @DisplayName("Should not start ride if user is passenger")
    @Order(19)
    public void shouldNotStartRideIfUserIsPassenger() {
        ResponseEntity<String> response = this.restTemplatePassenger.exchange(RIDE_URL_PATH + "/1/start",
                HttpMethod.PUT,
                null,
                String.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @DisplayName("Should delete favorite ride if user is passenger and it exists")
    @Order(20)
    public void shouldDeleteFavoriteRideIfUserIsPassengerAndItExists() {
        ResponseEntity<FavoriteRideDTO[]> ridesBeforeResponse = this.restTemplatePassenger.exchange(
                RIDE_URL_PATH + "/favorites/passenger/1",
                HttpMethod.GET,
                null,
                FavoriteRideDTO[].class);

        FavoriteRideDTO[] ridesBefore = ridesBeforeResponse.getBody();
        assertNotNull(ridesBefore);

        ResponseEntity<String> response = this.restTemplatePassenger.exchange(RIDE_URL_PATH + "/favorites/1",
                HttpMethod.DELETE,
                null,
                String.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        ResponseEntity<FavoriteRideDTO[]> ridesAfterResponse = this.restTemplatePassenger.exchange(
                RIDE_URL_PATH + "/favorites/passenger/1",
                HttpMethod.GET,
                null,
                FavoriteRideDTO[].class);

        FavoriteRideDTO[] ridesAfter = ridesAfterResponse.getBody();
        assertNotNull(ridesAfter);

        assertEquals(ridesBefore.length - 1, ridesAfter.length);
    }

    @Test
    @DisplayName("Should not delete favorite ride if it does not exist")
    @Order(21)
    public void shouldNotDeleteFavoriteRideIfItDoesNotExist() {
        ResponseEntity<String> response = this.restTemplatePassenger.exchange(RIDE_URL_PATH + "/favorites/99",
                HttpMethod.DELETE,
                null,
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(messageSource.getMessage("favorite.notFound", null, Locale.getDefault()), response.getBody());
    }

    @Test
    @DisplayName("Should not delete favorite ride if user is unauthorized")
    @Order(22)
    public void shouldNotDeleteFavoriteRideIfUserIsUnauthorized() {
        ResponseEntity<String> response = this.restTemplatePlain.exchange(RIDE_URL_PATH + "/favorites/1",
                HttpMethod.DELETE,
                null,
                String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("Should not delete favorite ride if user is admin")
    @Order(23)
    public void shouldNotDeleteFavoriteRideIfUserIsAdmin() {
        ResponseEntity<String> response = this.restTemplateAdmin.exchange(RIDE_URL_PATH + "/favorites/1",
                HttpMethod.DELETE,
                null,
                String.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @DisplayName("Should not delete favorite ride if user is driver")
    @Order(24)
    public void shouldNotDeleteFavoriteRideIfUserIsDriver() {
        ResponseEntity<String> response = this.restTemplateDriver.exchange(RIDE_URL_PATH + "/favorites/1",
                HttpMethod.DELETE,
                null,
                String.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @DisplayName("Should get all favorite rides for passenger if he exists and user is passenger")
    @Order(25)
    public void shouldGetAllFavoriteRidesForPassengerIfHeExistsAndUserIsPassenger() {
        ResponseEntity<FavoriteRideDTO[]> response = this.restTemplatePassenger.exchange(
                RIDE_URL_PATH + "/favorites/passenger/1",
                HttpMethod.GET,
                null,
                FavoriteRideDTO[].class);

        FavoriteRideDTO[] favoriteRides = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(favoriteRides);
        assertEquals(3, favoriteRides.length);

        //assert that one of the passengers inside each favorite ride has the id 1
        assertTrue(Arrays.stream(favoriteRides)
                .allMatch(favoriteRide -> favoriteRide.getPassengers().stream()
                        .anyMatch(passengerDTO -> passengerDTO.getId() == 1)));
    }

    @Test
    @DisplayName("Should not get all favorite rides for passenger if he does not exist")
    @Order(26)
    public void shouldNotGetAllFavoriteRidesForPassengerIfHeDoesNotExist() {
        ResponseEntity<String> response = this.restTemplatePassenger.exchange(
                RIDE_URL_PATH + "/favorites/passenger/99",
                HttpMethod.GET,
                null,
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(messageSource.getMessage("passenger.notFound", null, Locale.getDefault()), response.getBody());
    }

    @Test
    @DisplayName("Should not get all favorite rides if user is unauthorized")
    @Order(27)
    public void shouldNotGetAllFavoriteRidesIfUserIsUnauthorized() {
        ResponseEntity<String> response = this.restTemplatePlain.exchange(
                RIDE_URL_PATH + "/favorites/passenger/1",
                HttpMethod.GET,
                null,
                String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("Should not get all favorite rides if user is admin")
    @Order(28)
    public void shouldNotGetAllFavoriteRidesIfUserIsAdmin() {
        ResponseEntity<String> response = this.restTemplateAdmin.exchange(
                RIDE_URL_PATH + "/favorites/passenger/1",
                HttpMethod.GET,
                null,
                String.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @DisplayName("Should not get all favorite rides if user is driver")
    @Order(29)
    public void shouldNotGetAllFavoriteRidesIfUserIsDriver() {
        ResponseEntity<String> response = this.restTemplateDriver.exchange(
                RIDE_URL_PATH + "/favorites/passenger/1",
                HttpMethod.GET,
                null,
                String.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
}
