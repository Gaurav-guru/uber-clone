package rs.ac.uns.ftn.transport.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import rs.ac.uns.ftn.transport.model.Driver;
import rs.ac.uns.ftn.transport.repository.DriverRepository;
import rs.ac.uns.ftn.transport.service.interfaces.IDriverService;

import java.util.Optional;

@Service
public class DriverServiceImpl implements IDriverService {

    private final DriverRepository driverRepository;

    public DriverServiceImpl(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    public Page<Driver> findAll(Pageable page) {
        return driverRepository.findAll(page);
    }

    public Driver findOne(Integer id) {
        Optional<Driver> found = driverRepository.findById(id);
        if (found.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return found.get();
    }

    public Driver save(Driver driver) {
        return driverRepository.save(driver);
    }
}
