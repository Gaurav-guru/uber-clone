package rs.ac.uns.ftn.transport.controller;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.transport.dto.RouteDTO;
import rs.ac.uns.ftn.transport.dto.ride.RideCreatedDTO;
import rs.ac.uns.ftn.transport.dto.ride.RideCreationDTO;
import rs.ac.uns.ftn.transport.dto.ride.RideEstimationDTO;
import rs.ac.uns.ftn.transport.mapper.ride.RideCreationDTOMapper;
import rs.ac.uns.ftn.transport.model.Location;
import rs.ac.uns.ftn.transport.model.Ride;
import rs.ac.uns.ftn.transport.model.Route;
import rs.ac.uns.ftn.transport.service.EstimatesService;

import java.util.Set;

@CrossOrigin("http://localhost:4200")
@RestController
@RequestMapping(value="/api/unregisteredUser")
public class UnregisteredUserController {


    private final EstimatesService estimatesService;
    private final RideCreationDTOMapper rideCreationDTOMapper;

    public UnregisteredUserController(RideCreationDTOMapper rideCreationDTOMapper,EstimatesService estimatesService) {
        this.rideCreationDTOMapper = rideCreationDTOMapper;
        this.estimatesService = estimatesService;
    }

    @PostMapping
    public ResponseEntity<RideEstimationDTO> getEstimation(@RequestBody RideCreationDTO ride){
        RideEstimationDTO rideEstimationDTO = new RideEstimationDTO();
        /*Ride ride1 = rideCreationDTOMapper.fromDTOtoRide(ride);

        Location dep = new Location();
        dep.setLongitude(ride.getLocations().iterator().next().getDeparture().getLongitude());
        dep.setLatitude(ride.getLocations().iterator().next().getDeparture().getLatitude());

        Location des = new Location();
        des.setLongitude(ride.getLocations().iterator().next().getDestination().getLongitude());
        des.setLatitude(ride.getLocations().iterator().next().getDestination().getLatitude());

        double distance = estimatesService.calculateDistance(dep, des);


        double price = estimatesService.getEstimatedPrice(ride1.getVehicleType(), distance);

        double time = estimatesService.getEstimatedTime(distance);

        rideEstimationDTO.setEstimatedCost(price);
        rideEstimationDTO.setEstimatedTimeInMinutes(time);*/


        rideEstimationDTO.setEstimatedCost(ThreadLocalRandom.current().nextInt(100, 300 + 1));
        rideEstimationDTO.setEstimatedTimeInMinutes(ThreadLocalRandom.current().nextInt(2, 5 + 1));
        return new ResponseEntity<>(rideEstimationDTO, HttpStatus.OK);
    }
}
