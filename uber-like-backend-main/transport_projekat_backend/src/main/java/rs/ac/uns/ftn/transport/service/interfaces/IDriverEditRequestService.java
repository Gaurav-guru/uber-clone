package rs.ac.uns.ftn.transport.service.interfaces;

import rs.ac.uns.ftn.transport.model.DriverEditRequest;

public interface IDriverEditRequestService {
    DriverEditRequest save(DriverEditRequest driverEditRequest);

    DriverEditRequest findByDriverId(Integer id);
}
