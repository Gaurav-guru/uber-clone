package rs.ac.uns.ftn.transport.controller;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import rs.ac.uns.ftn.transport.dto.review.*;
import rs.ac.uns.ftn.transport.mapper.review.DriverReviewDTOMapper;
import rs.ac.uns.ftn.transport.mapper.review.VehicleReviewDTOMapper;
import rs.ac.uns.ftn.transport.model.DriverReview;
import rs.ac.uns.ftn.transport.model.Ride;
import rs.ac.uns.ftn.transport.model.VehicleReview;
import rs.ac.uns.ftn.transport.repository.PassengerRepository;
import rs.ac.uns.ftn.transport.service.interfaces.*;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin("http://localhost:4200")
@RestController
@RequestMapping(value="api/review")
public class ReviewController {
    private final IReviewService reviewService;
    private final IRideService rideService;
    private final MessageSource messageSource;
    private final IDriverService driverService;
    private final IUserService userService;
    private final IVehicleService vehicleService;
    private final PassengerRepository passengerRepository;

    public ReviewController(MessageSource messageSource, IReviewService reviewService, IVehicleService vehicleService, IDriverService driverService,
                            IRideService rideService, IUserService userService,
                            PassengerRepository passengerRepository){
        this.messageSource = messageSource;
        this.rideService = rideService;
        this.reviewService = reviewService;
        this.vehicleService = vehicleService;
        this.driverService = driverService;
        this.userService = userService;
        this.passengerRepository = passengerRepository;
    }

    @PostMapping(value = "/{rideId}/vehicle", consumes = "application/json")
    public ResponseEntity<?> saveVehicleReview(@PathVariable Integer rideId,
                                                              @RequestBody VehicleReviewDTO review){
        try {
            VehicleReview vehicleReview = new VehicleReview();
            Ride currentRide = rideService.findOne(rideId);
            vehicleReview.setCurrentRide(currentRide);
            vehicleReview.setRating(review.getRating());
            vehicleReview.setComment(review.getComment());
            vehicleReview.setVehicle(vehicleReview.getCurrentRide().getDriver().getVehicle());
            vehicleReview.setReviewer(passengerRepository.findById(review.getReviewer().getId()).get());
            review = VehicleReviewDTOMapper.fromVehicleReviewtoDTO(reviewService.saveVehicleReview(vehicleReview));
            return new ResponseEntity<>(review, HttpStatus.OK);
        }   catch (ResponseStatusException e) {
            return new ResponseEntity<>(messageSource.getMessage("ride.vehicle.notFound", null, Locale.getDefault()), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "vehicle/{id}")
    public ResponseEntity<?> getVehicleReviewsForVehicle(@PathVariable Integer id){
        try {
            Set<VehicleReview> reviews = reviewService.getVehicleReviewsofVehicle(id);
            Set<VehicleReviewDTO> vehicleReviewDTOS = reviews.stream()
                    .map(VehicleReviewDTOMapper::fromVehicleReviewtoDTO)
                    .collect(Collectors.toSet());
            return new ResponseEntity<>(new VehicleReviewPageDTO((long) reviews.size(), vehicleReviewDTOS), HttpStatus.OK);
        }   catch (ResponseStatusException e){
            return new ResponseEntity<>(messageSource.getMessage("vehicle.review.notFound", null, Locale.getDefault()), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value = "{rideId}/driver", consumes = "application/json")
    public ResponseEntity<?> saveDriverReview(@PathVariable Integer rideId, @RequestBody DriverReviewDTO review){
        try {
            DriverReview driverReview = new DriverReview();
            Ride currentRide = rideService.findOne(rideId);
            driverReview.setCurrentRide(currentRide);
            driverReview.setDriver(driverReview.getCurrentRide().getDriver());
            driverReview.setRating(review.getRating());
            driverReview.setComment(review.getComment());
            driverReview.setReviewer(passengerRepository.findById(review.getReviewer().getId()).get());
            review = DriverReviewDTOMapper.fromDriverReviewToDTO(reviewService.saveDriverReview(driverReview));
            return new ResponseEntity<>(review, HttpStatus.OK);
        }   catch (ResponseStatusException e){
            return new ResponseEntity<>(messageSource.getMessage("ride.notFound", null, Locale.getDefault()), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "driver/{id}")
    public ResponseEntity<?> getDriverReviewsForDriver(@PathVariable Integer id){
        try {
            Set<DriverReview> reviews = reviewService.getDriverReviewsofDriver(id);

            Set<DriverReviewDTO> driverReviewDTOS = reviews.stream()
                    .map(DriverReviewDTOMapper::fromDriverReviewToDTO)
                    .collect(Collectors.toSet());

            return new ResponseEntity<>(new DriverReviewPageDTO((long) reviews.size(), driverReviewDTOS), HttpStatus.OK);
        } catch (ResponseStatusException e){
            return new ResponseEntity<>(messageSource.getMessage("driver.notFound", null, Locale.getDefault()), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/{rideId}")
    public ResponseEntity<?> getReviewsForRide(@PathVariable Integer rideId){
        try {
            ReviewRideDTO reviewRideDTO = reviewService.getReviewsForRide(rideId);
            return new ResponseEntity<>(reviewRideDTO, HttpStatus.OK);
        }   catch (ResponseStatusException e){
            return new ResponseEntity<>(messageSource.getMessage("ride.notFound", null, Locale.getDefault()), HttpStatus.NOT_FOUND);
        }
    }
}
