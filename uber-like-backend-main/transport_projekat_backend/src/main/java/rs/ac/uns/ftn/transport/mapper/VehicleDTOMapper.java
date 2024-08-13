package rs.ac.uns.ftn.transport.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.transport.dto.VehicleDTO;
import rs.ac.uns.ftn.transport.model.Vehicle;
import rs.ac.uns.ftn.transport.service.interfaces.IVehicleTypeService;

@Component
public class VehicleDTOMapper {
    private static ModelMapper modelMapper;
    private static IVehicleTypeService vehicleTypeService;

    @Autowired
    public VehicleDTOMapper(ModelMapper modelMapper,
                            IVehicleTypeService vehicleTypeService) {
        VehicleDTOMapper.modelMapper = modelMapper;
        VehicleDTOMapper.vehicleTypeService = vehicleTypeService;
    }

    public static Vehicle fromDTOtoVehicle(VehicleDTO dto) {
        Vehicle vehicle = modelMapper.map(dto, Vehicle.class);
        vehicle.setVehicleType(vehicleTypeService.findByName(dto.getVehicleType().toUpperCase()));
        return vehicle;
    }

    public static VehicleDTO fromVehicletoDTO(Vehicle vehicle) {
        String vehicleType = vehicle.getVehicleType().getName();
        VehicleDTO vehicleDTO = modelMapper.map(vehicle, VehicleDTO.class);
        vehicleDTO.setVehicleType(vehicleType);
        return vehicleDTO;
    }
}