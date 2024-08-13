package rs.ac.uns.ftn.transport.service;

import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.transport.model.DriverEditRequest;
import rs.ac.uns.ftn.transport.repository.DriverEditRequestRepository;
import rs.ac.uns.ftn.transport.service.interfaces.IDriverEditRequestService;

@Service
public class DriverEditRequestServiceImpl implements IDriverEditRequestService {
    private final DriverEditRequestRepository driverEditRequestRepository;

    public DriverEditRequestServiceImpl(DriverEditRequestRepository driverEditRequestRepository) {
        this.driverEditRequestRepository = driverEditRequestRepository;
    }

    public DriverEditRequest save(DriverEditRequest driverEditRequest) {
        return driverEditRequestRepository.save(driverEditRequest);
    }

    public DriverEditRequest findByDriverId(Integer id) {
        return driverEditRequestRepository.findByDriverId(id);
    }
}
