package rs.ac.uns.ftn.transport.service;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import rs.ac.uns.ftn.transport.model.Driver;
import rs.ac.uns.ftn.transport.model.Location;
import rs.ac.uns.ftn.transport.model.Vehicle;
import rs.ac.uns.ftn.transport.repository.DriverRepository;
import rs.ac.uns.ftn.transport.repository.VehicleRepository;
import rs.ac.uns.ftn.transport.service.interfaces.IVehicleService;

import java.util.Locale;
import java.util.Optional;

@Service
public class VehicleServiceImpl implements IVehicleService {

    private final VehicleRepository vehicleRepository;
    private final MessageSource messageSource;
    private final DriverRepository driverRepository;

    public VehicleServiceImpl(VehicleRepository vehicleRepository,
                              MessageSource messageSource,
                              DriverRepository driverRepository) {
        this.vehicleRepository = vehicleRepository;
        this.messageSource = messageSource;
        this.driverRepository = driverRepository;
    }

    public Vehicle save(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    public Vehicle getVehicleByDriver_Id(Integer id) {
        return vehicleRepository.getVehicleByDriver_Id(id);
    }

    public Vehicle getVehicleById(Integer id){return vehicleRepository.findById(id).orElseGet(null);}

    @Override
    public Vehicle changeLocation(Integer id, Location newLocation) {
        Optional<Vehicle> toChange = vehicleRepository.findById(id);
        if(toChange.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,messageSource.getMessage("vehicle.nonExisting", null, Locale.getDefault()));
        }
        Optional<Driver> driver = driverRepository.getDriverByVehicle_Id(id);
        if(driver.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,messageSource.getMessage("vehicle.notFound", null, Locale.getDefault()));
        }
        Vehicle change = toChange.get();
        change.setCurrentLocation(newLocation);
        vehicleRepository.save(change);
        return change;
    }
}