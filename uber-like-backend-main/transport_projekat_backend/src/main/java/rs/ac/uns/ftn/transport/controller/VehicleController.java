package rs.ac.uns.ftn.transport.controller;

import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import rs.ac.uns.ftn.transport.dto.LocationDTO;
import rs.ac.uns.ftn.transport.dto.VehicleSimulationDTO;
import rs.ac.uns.ftn.transport.mapper.LocationDTOMapper;
import rs.ac.uns.ftn.transport.model.Location;
import rs.ac.uns.ftn.transport.model.ResponseMessage;
import rs.ac.uns.ftn.transport.model.Vehicle;
import rs.ac.uns.ftn.transport.service.interfaces.IVehicleService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value="/api/vehicle")
public class VehicleController {

    private final IVehicleService vehicleService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public VehicleController(IVehicleService vehicleService, SimpMessagingTemplate simpMessagingTemplate) {
        this.vehicleService = vehicleService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @PutMapping(value = "/{id}/location", consumes = "application/json")
    public ResponseEntity<?> changeLocation(@PathVariable Integer id, @Valid @RequestBody LocationDTO currentLocation)
    {
        try {
            Location newLocation = LocationDTOMapper.fromDTOtoLocation(currentLocation);
            Vehicle vehicle = vehicleService.changeLocation(id, newLocation);
            VehicleSimulationDTO vehicleSimulationDTO = new VehicleSimulationDTO(vehicle);
            this.simpMessagingTemplate.convertAndSend("/map-updates/update-vehicle-position", vehicleSimulationDTO);
            return new ResponseEntity<>("Coordinates successfully updated!", HttpStatus.NO_CONTENT);
        }
        catch(ResponseStatusException ex){
            if(ex.getStatus() == HttpStatus.NOT_FOUND) {
                return new ResponseEntity<>(ex.getReason(), ex.getStatus());
            }
            return new ResponseEntity<>(new ResponseMessage(ex.getReason()), ex.getStatus());
        }
    }

}
