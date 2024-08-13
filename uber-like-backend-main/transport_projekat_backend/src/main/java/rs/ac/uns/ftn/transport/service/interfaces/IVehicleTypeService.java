package rs.ac.uns.ftn.transport.service.interfaces;

import rs.ac.uns.ftn.transport.model.VehicleType;

public interface IVehicleTypeService {
    VehicleType findByName(String name);
}