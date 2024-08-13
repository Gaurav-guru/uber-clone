package rs.ac.uns.ftn.transport.controller;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.MessageSource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import rs.ac.uns.ftn.transport.dto.LocationDTO;
import rs.ac.uns.ftn.transport.dto.RejectionReasonDTO;
import rs.ac.uns.ftn.transport.dto.RouteDTO;
import rs.ac.uns.ftn.transport.dto.TokenDTO;
import rs.ac.uns.ftn.transport.dto.User.LoginDTO;
import rs.ac.uns.ftn.transport.dto.panic.PanicReasonDTO;
import rs.ac.uns.ftn.transport.dto.passenger.PassengerIdEmailDTO;
import rs.ac.uns.ftn.transport.dto.ride.FavoriteRideDTO;
import rs.ac.uns.ftn.transport.dto.ride.FavoriteRideWithoutIdDTO;
import rs.ac.uns.ftn.transport.dto.ride.RideCreatedDTO;
import rs.ac.uns.ftn.transport.dto.ride.RideCreationDTO;
import rs.ac.uns.ftn.transport.model.enumerations.RideStatus;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ThirdRideControllerIntegrationTests {
    @Autowired
    private TestRestTemplate restTemplatePlain;
    @Autowired
    private MessageSource messageSource;
    private TestRestTemplate restTemplatePassenger,restTemplatePassengerSecond,restTemplateDriver,restTemplateAdmin;
    private String passengerJwtToken,passengerJwtTokenSecond,driverJwtToken,adminJwtToken;

    private final String BASE_URL_PATH = "http://localhost:8080/api";

    private void instantiatePassengerRestTemplate(){
        RestTemplateBuilder builder = new RestTemplateBuilder(rt-> rt.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer "+ this.passengerJwtToken);
            return execution.execute(request, body);
        }));
        this.restTemplatePassenger = new TestRestTemplate(builder);
        RestTemplateBuilder builderSecond = new RestTemplateBuilder(rt-> rt.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer "+ this.passengerJwtTokenSecond);
            return execution.execute(request, body);
        }));
        this.restTemplatePassengerSecond = new TestRestTemplate(builderSecond);
    }

    private void instantiateDriverRestTemplate(){
        RestTemplateBuilder builder = new RestTemplateBuilder(rt-> rt.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer "+ this.driverJwtToken);
            return execution.execute(request, body);
        }));
        this.restTemplateDriver = new TestRestTemplate(builder);
    }

    private void instantiateAdminRestTemplate(){
        RestTemplateBuilder builder = new RestTemplateBuilder(rt-> rt.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer "+ this.adminJwtToken);
            return execution.execute(request, body);
        }));
        this.restTemplateAdmin = new TestRestTemplate(builder);
    }

    private void instantiateAllJwtTokens(){

        HttpEntity<LoginDTO> driverLoginCredentials = new HttpEntity<>(new LoginDTO("driver1@mail.com","Test1Test"));
        ResponseEntity<TokenDTO> loginDriver = this.restTemplatePlain.exchange("/api/user/login",
                HttpMethod.POST,
                driverLoginCredentials,
                TokenDTO.class);
        this.driverJwtToken = loginDriver.getBody().getAccessToken();

        HttpEntity<LoginDTO> passengerLoginCredentials = new HttpEntity<>(new LoginDTO("passenger1@mail.com","Test1Test"));
        ResponseEntity<TokenDTO> loginPassenger = restTemplatePlain.exchange("/api/user/login",
                HttpMethod.POST,
                passengerLoginCredentials,
                TokenDTO.class);
        this.passengerJwtToken = loginPassenger.getBody().getAccessToken();

        passengerLoginCredentials = new HttpEntity<>(new LoginDTO("passenger2@mail.com","Test1Test"));
        ResponseEntity<TokenDTO> loginPassengerSecond = restTemplatePlain.exchange("/api/user/login",
                HttpMethod.POST,
                passengerLoginCredentials,
                TokenDTO.class);
        this.passengerJwtTokenSecond = loginPassengerSecond.getBody().getAccessToken();

        HttpEntity<LoginDTO> adminLoginCredentials = new HttpEntity<>(new LoginDTO("admin1@mail.com","Test1Test"));
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
    @DisplayName("Should create favourite ride if ride exists")
    @Order(1)
    public void ShouldCreateFavouriteRideIfRideExists(){
        LocationDTO location = new LocationDTO("Bulevar oslobodjenja 46", 45.0, 19.833549);
        RouteDTO route = new RouteDTO(location, location);

        PassengerIdEmailDTO passenger = new PassengerIdEmailDTO(1, "passenger1@mail.com");
        PassengerIdEmailDTO passengerSecond = new PassengerIdEmailDTO(2, "passenger2@mail.com");

        Set<RouteDTO> locations = new HashSet<>(Set.of(route));
        Set<PassengerIdEmailDTO> passengers = new HashSet<>(Set.of(passenger, passengerSecond));

        FavoriteRideWithoutIdDTO mockDTO = new FavoriteRideWithoutIdDTO();
        mockDTO.setFavoriteName("fav name");
        mockDTO.setVehicleType("STANDARD");
        mockDTO.setLocations(locations);
        mockDTO.setPassengers(passengers);
        mockDTO.setPetTransport(true);
        mockDTO.setBabyTransport(true);

        ResponseEntity<FavoriteRideDTO> favoriteRideDTOResponseEntity =
                this.restTemplatePassenger.exchange(this.BASE_URL_PATH + "/ride/favorites", HttpMethod.POST,
                        new HttpEntity<>(mockDTO), FavoriteRideDTO.class);
        FavoriteRideDTO favoriteRideDTO = favoriteRideDTOResponseEntity.getBody();

        assertEquals(HttpStatus.OK, favoriteRideDTOResponseEntity.getStatusCode());
        assertNotNull(favoriteRideDTO);
    }

    @Test
    @DisplayName("Should not create favourite ride if ride does not exist")
    @Order(2)
    public void shouldNotCreateFavouriteRideIfRideDoesNotExist(){
        ResponseEntity<String> response = this.restTemplatePassenger.exchange(this.BASE_URL_PATH + "/ride/favorites",
                HttpMethod.POST,
                null,
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should not create favourite ride if unauthorized")
    @Order(3)
    public void shouldNotCreateFavouriteRideIfUnauthorized(){
        ResponseEntity<String> response = this.restTemplatePlain.exchange(this.BASE_URL_PATH + "/ride/favorites",
                HttpMethod.POST,
                null,
                String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("Should not create favourite ride if user is driver")
    @Order(4)
    public void shouldNotCreateFavouriteRideIfUserIsDriver(){
        ResponseEntity<String> response = this.restTemplateDriver.exchange(this.BASE_URL_PATH + "/ride/favorites",
                HttpMethod.POST,
                null,
                String.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @DisplayName("Should not create favourite ride if user is admin")
    @Order(5)
    public void shouldNotCreateFavouriteRideIfUserIsAdmin(){
        ResponseEntity<String> response = this.restTemplateAdmin.exchange(this.BASE_URL_PATH + "/ride/favorites",
                HttpMethod.POST,
                null,
                String.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @DisplayName("Should accept ride if ride id is correct")
    @Order(5)
    public void shouldAcceptRideIfRideIdIsCorrect(){
        LocationDTO location = new LocationDTO("Bulevar oslobodjenja 46",45.0,19.833549);
        RouteDTO route = new RouteDTO(location,location);
        PassengerIdEmailDTO passenger = new PassengerIdEmailDTO(1,"passenger1@mail.com");
        Set<RouteDTO> locations = new HashSet<>();
        locations.add(route);
        Set<PassengerIdEmailDTO> passengers = new HashSet<>();
        passengers.add(passenger);
        RideCreationDTO rideOrder = new RideCreationDTO(locations,
                passengers,
                "STANDARD",
                true,
                true,
                null);

        this.restTemplatePassenger.exchange(this.BASE_URL_PATH + "/ride",
                HttpMethod.POST,new HttpEntity<>(rideOrder),String.class);

        ResponseEntity<String> response = this.restTemplateDriver.exchange(this.BASE_URL_PATH + "/ride/1/accept",
                HttpMethod.PUT,
                null,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Should not accept ride if ride id is incorrect format")
    @Order(6)
    public void shouldNotAcceptRideIfRideIdIsIncorrectFormat(){
        ResponseEntity<String> response = this.restTemplateDriver.exchange(this.BASE_URL_PATH + "/ride/faefea/accept",
                HttpMethod.PUT,
                null,
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should not accept ride if ride does not exist")
    @Order(7)
    public void shouldNotAcceptRideIfRideDoesNotExist(){
        ResponseEntity<String> response = this.restTemplateDriver.exchange(this.BASE_URL_PATH + "/ride/99999/accept",
                HttpMethod.PUT,
                null,
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Should not accept ride if unauthorized")
    @Order(8)
    public void shouldNotAcceptRideIfUnauthorized(){
        ResponseEntity<String> response = this.restTemplatePlain.exchange(this.BASE_URL_PATH + "/ride/1/accept",
                HttpMethod.PUT,
                null,
                String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("Should not accept ride if passenger")
    @Order(9)
    public void shouldNotAcceptRideIfPassenger(){
        ResponseEntity<String> response = this.restTemplatePassenger.exchange(this.BASE_URL_PATH + "/ride/1/accept",
                HttpMethod.PUT,
                null,
                String.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @DisplayName("Should not accept ride if admin")
    @Order(10)
    public void shouldNotAcceptRideIfAdmin(){
        ResponseEntity<String> response = this.restTemplateAdmin.exchange(this.BASE_URL_PATH + "/ride/1/accept",
                HttpMethod.PUT,
                null,
                String.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @DisplayName("Should not end ride if it has not started")
    @Order(11)
    public void shouldNotEndRideIfItHasNotStarted(){
        ResponseEntity<String> response = this.restTemplateDriver.exchange(this.BASE_URL_PATH + "/ride/1/end",
                HttpMethod.PUT,
                null,
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should end ride if ride id is correct")
    @Order(12)
    public void shouldEndRideIfRideIdIsCorrect() {
        this.restTemplateDriver.exchange(BASE_URL_PATH + "/ride/1/start",
                HttpMethod.PUT,
                null,
                RideCreatedDTO.class);

        ResponseEntity<String> response = this.restTemplateDriver.exchange(this.BASE_URL_PATH + "/ride/1/end",
                HttpMethod.PUT,
                null,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Should not end ride if unauthorized")
    @Order(13)
    public void shouldNotEndRideIfUnauthorized(){
        ResponseEntity<String> response = this.restTemplatePlain.exchange(this.BASE_URL_PATH + "/ride/1/end",
                HttpMethod.PUT,
                null,
                String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("Should not end ride if passenger")
    @Order(14)
    public void shouldNotEndRideIfPassenger(){
        ResponseEntity<String> response = this.restTemplatePassenger.exchange(this.BASE_URL_PATH + "/ride/1/end",
                HttpMethod.PUT,
                null,
                String.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @DisplayName("Should not end ride if admin")
    @Order(15)
    public void shouldNotEndRideIfAdmin(){
        ResponseEntity<String> response = this.restTemplateAdmin.exchange(this.BASE_URL_PATH + "/ride/1/end",
                HttpMethod.PUT,
                null,
                String.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @DisplayName("Should not end ride if ride id is incorrect format")
    @Order(16)
    public void shouldNotEndRideIfRideIdIsIncorrectFormat(){
        ResponseEntity<String> response = this.restTemplateDriver.exchange(this.BASE_URL_PATH + "/ride/faefea/end",
                HttpMethod.PUT,
                null,
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should not end ride if ride does not exist")
    @Order(17)
    public void ShouldNotEndRideIfRideDoesNotExist(){
        ResponseEntity<String> response = this.restTemplateDriver.exchange(this.BASE_URL_PATH + "/ride/999999/end",
                HttpMethod.PUT,
                null,
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Should withdraw from ride if ride exists and is pending")
    @Order(18)
    public void ShouldWithdrawFromRideIfRideExistsAndIsPending(){
        LocationDTO location = new LocationDTO("Bulevar oslobodjenja 46",45.0,19.833549);
        RouteDTO route = new RouteDTO(location,location);
        PassengerIdEmailDTO passenger = new PassengerIdEmailDTO(1,"passenger1@mail.com");
        Set<RouteDTO> locations = new HashSet<>();
        locations.add(route);
        Set<PassengerIdEmailDTO> passengers = new HashSet<>();
        passengers.add(passenger);
        RideCreationDTO rideOrder = new RideCreationDTO(locations,
                passengers,
                "STANDARD",
                true,
                true,
                null);

        this.restTemplatePassenger.exchange(this.BASE_URL_PATH + "/ride",
                HttpMethod.POST,new HttpEntity<>(rideOrder),String.class);

        ResponseEntity<String> response = this.restTemplatePassenger.exchange(this.BASE_URL_PATH + "/ride/2/withdraw",
                HttpMethod.PUT,
                null,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Should withdraw from ride if ride exists and is accepted")
    @Order(19)
    public void ShouldWithdrawFromRideIfRideExistsAndIsAccepted() {
        LocationDTO location = new LocationDTO("Bulevar oslobodjenja 46",45.0,19.833549);
        RouteDTO route = new RouteDTO(location,location);
        PassengerIdEmailDTO passenger = new PassengerIdEmailDTO(1,"passenger1@mail.com");
        Set<RouteDTO> locations = new HashSet<>();
        locations.add(route);
        Set<PassengerIdEmailDTO> passengers = new HashSet<>();
        passengers.add(passenger);
        RideCreationDTO rideOrder = new RideCreationDTO(locations,
                passengers,
                "STANDARD",
                true,
                true,
                null);

        this.restTemplatePassenger.exchange(this.BASE_URL_PATH + "/ride",
                HttpMethod.POST,new HttpEntity<>(rideOrder),String.class);

        this.restTemplateDriver.exchange(this.BASE_URL_PATH + "/ride/3/accept",
                HttpMethod.PUT,
                null,
                String.class);

        ResponseEntity<String> response = this.restTemplatePassenger.exchange(this.BASE_URL_PATH + "/ride/3/withdraw",
                HttpMethod.PUT,
                null,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Should not withdraw from ride if ride does not exist")
    @Order(20)
    public void ShouldNotWithdrawFromRideIfRideDoesNotExist(){
        ResponseEntity<String> response = this.restTemplatePassenger.exchange(this.BASE_URL_PATH + "/ride/999999/withdraw",
                HttpMethod.PUT,
                null,
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Should not withdraw from ride if ride id is incorrect format")
    @Order(21)
    public void ShouldNotWithdrawFromRideIfRideIdIsIncorrectFormat(){
        ResponseEntity<String> response = this.restTemplatePassenger.exchange(this.BASE_URL_PATH + "/ride/feafae/withdraw",
                HttpMethod.PUT,
                null,
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should not withdraw from ride if unauthorized")
    @Order(22)
    public void ShouldNotWithdrawFromRideIfUnauthorized(){
        LocationDTO location = new LocationDTO("Bulevar oslobodjenja 46",45.0,19.833549);
        RouteDTO route = new RouteDTO(location,location);
        PassengerIdEmailDTO passenger = new PassengerIdEmailDTO(1,"passenger1@mail.com");
        Set<RouteDTO> locations = new HashSet<>();
        locations.add(route);
        Set<PassengerIdEmailDTO> passengers = new HashSet<>();
        passengers.add(passenger);
        RideCreationDTO rideOrder = new RideCreationDTO(locations,
                passengers,
                "STANDARD",
                true,
                true,
                null);

        this.restTemplatePassenger.exchange(this.BASE_URL_PATH + "/ride",
                HttpMethod.POST,new HttpEntity<>(rideOrder),String.class);

        ResponseEntity<String> response = this.restTemplatePlain.exchange(this.BASE_URL_PATH + "/ride/4/withdraw",
                HttpMethod.PUT,
                null,
                String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("Should not withdraw from ride if driver")
    @Order(23)
    public void ShouldNotWithdrawFromRideIfDriver(){
        ResponseEntity<String> response = this.restTemplateDriver.exchange(this.BASE_URL_PATH + "/ride/4/withdraw",
                HttpMethod.PUT,
                null,
                String.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @DisplayName("Should not withdraw from ride if admin")
    @Order(24)
    public void ShouldNotWithdrawFromRideIfAdmin(){
        ResponseEntity<String> response = this.restTemplateAdmin.exchange(this.BASE_URL_PATH + "/ride/4/withdraw",
                HttpMethod.PUT,
                null,
                String.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @DisplayName("Should not withdraw from ride if ride is not pending or accepted")
    @Order(25)
    public void ShouldNotWithdrawFromRideIfRideIsNotPendingOrAccepted(){
        ResponseEntity<String> response = this.restTemplatePassenger.exchange(this.BASE_URL_PATH + "/ride/1/withdraw",
                HttpMethod.PUT,
                null,
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should not cancel ride if ride does not exist")
    @Order(26)
    public void ShouldNotCancelRideIfRideDoesNotExist(){
        RejectionReasonDTO rejectionReason = new RejectionReasonDTO("Reason");

        ResponseEntity<String> response = this.restTemplateDriver.exchange(this.BASE_URL_PATH + "/ride/999999/cancel",
                HttpMethod.PUT,
                new HttpEntity<>(rejectionReason),
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Should not cancel ride if ride id is incorrect format")
    @Order(27)
    public void ShouldNotCancelRideIfRideIdIsIncorrectFormat(){
        RejectionReasonDTO rejectionReason = new RejectionReasonDTO("Reason");

        ResponseEntity<String> response = this.restTemplateDriver.exchange(this.BASE_URL_PATH + "/ride/feafae/cancel",
                HttpMethod.PUT,
                new HttpEntity<>(rejectionReason),
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should not cancel ride if reason is null")
    @Order(28)
    public void ShouldNotCancelRideIfReasonIsNull(){
        RejectionReasonDTO rejectionReason = new RejectionReasonDTO("");

        ResponseEntity<String> response = this.restTemplateDriver.exchange(this.BASE_URL_PATH + "/ride/4/cancel",
                HttpMethod.PUT,
                new HttpEntity<>(rejectionReason),
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should not cancel ride if reason is empty")
    @Order(29)
    public void ShouldNotCancelRideIfReasonIsEmpty(){
        RejectionReasonDTO rejectionReason = new RejectionReasonDTO(" ");

        ResponseEntity<String> response = this.restTemplateDriver.exchange(this.BASE_URL_PATH + "/ride/4/cancel",
                HttpMethod.PUT,
                new HttpEntity<>(rejectionReason),
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should not cancel ride if reason is too long")
    @Order(30)
    public void ShouldNotCancelRideIfReasonIsTooLong() {
        RejectionReasonDTO rejectionReason = new RejectionReasonDTO("REASONREASONREASON".repeat(50));

        ResponseEntity<String> response = this.restTemplateDriver.exchange(this.BASE_URL_PATH + "/ride/4/cancel",
                HttpMethod.PUT,
                new HttpEntity<>(rejectionReason),
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should not cancel ride if unauthorized")
    @Order(31)
    public void ShouldNotCancelRideIfUnauthorized(){
        RejectionReasonDTO rejectionReason = new RejectionReasonDTO("Reason");

        ResponseEntity<String> response = this.restTemplatePlain.exchange(this.BASE_URL_PATH + "/ride/4/cancel",
                HttpMethod.PUT,
                new HttpEntity<>(rejectionReason),
                String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("Should not cancel ride if admin")
    @Order(32)
    public void ShouldNotCancelRideIfAdmin(){
        RejectionReasonDTO rejectionReason = new RejectionReasonDTO("Reason");

        ResponseEntity<String> response = this.restTemplateAdmin.exchange(this.BASE_URL_PATH + "/ride/4/cancel",
                HttpMethod.PUT,
                new HttpEntity<>(rejectionReason),
                String.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @DisplayName("Should not cancel ride if passenger")
    @Order(33)
    public void ShouldNotCancelRideIfPassenger(){
        ResponseEntity<String> response = this.restTemplatePassenger.exchange(this.BASE_URL_PATH + "/ride/1/end",
                HttpMethod.PUT,
                null,
                String.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @DisplayName("Should cancel ride if ride exists and reason is valid")
    @Order(34)
    public void ShouldCancelRideIfRideExistsAndReasonIsValid(){
        RejectionReasonDTO rejectionReason = new RejectionReasonDTO("Reason");

        ResponseEntity<String> response = this.restTemplateDriver.exchange(this.BASE_URL_PATH + "/ride/4/cancel",
                HttpMethod.PUT,
                new HttpEntity<>(rejectionReason),
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
