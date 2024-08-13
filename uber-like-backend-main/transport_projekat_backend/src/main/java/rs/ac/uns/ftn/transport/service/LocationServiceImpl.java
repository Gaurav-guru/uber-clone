package rs.ac.uns.ftn.transport.service;

import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.transport.model.Location;
import rs.ac.uns.ftn.transport.repository.LocationRepository;
import rs.ac.uns.ftn.transport.service.interfaces.ILocationService;

@Service
public class LocationServiceImpl implements ILocationService {
    private final LocationRepository locationRepository;

    public LocationServiceImpl(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public Location save(Location location) {
        return locationRepository.save(location);
    }
}