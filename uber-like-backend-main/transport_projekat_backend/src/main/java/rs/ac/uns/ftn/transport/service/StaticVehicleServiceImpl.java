package rs.ac.uns.ftn.transport.service;

import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.transport.model.VehicleType;
import rs.ac.uns.ftn.transport.service.interfaces.IVehicleTypeService;

@Service
public class StaticVehicleServiceImpl {

    private static IVehicleTypeService vehicleTypeService = null;

    public StaticVehicleServiceImpl(IVehicleTypeService vehicleTypeService) {
        this.vehicleTypeService = vehicleTypeService;
    }

    public static VehicleType findByName(String name) {
        return vehicleTypeService.findByName(name);
    }
}
