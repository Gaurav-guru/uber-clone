package rs.ac.uns.ftn.transport.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rs.ac.uns.ftn.transport.model.Driver;

public interface IDriverService {
    Page<Driver> findAll(Pageable page);
    Driver findOne(Integer id);
    Driver save(Driver driver);
}
