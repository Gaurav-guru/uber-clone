package rs.ac.uns.ftn.transport.service;

import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.transport.model.VehicleType;
import rs.ac.uns.ftn.transport.repository.VehicleTypeRepository;
import rs.ac.uns.ftn.transport.service.interfaces.IVehicleTypeService;

@Service
public class VehicleTypeServiceImpl implements IVehicleTypeService {
    private final VehicleTypeRepository vehicleTypeRepository;

    public VehicleTypeServiceImpl(VehicleTypeRepository vehicleTypeRepository) {
        this.vehicleTypeRepository = vehicleTypeRepository;
    }

    public  VehicleType findByName(String name) {
        return vehicleTypeRepository.findByName(name);
    }
}