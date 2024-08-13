package rs.ac.uns.ftn.transport.mapper.ride;

import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.transport.dto.ride.RideCreatedDTO;
import rs.ac.uns.ftn.transport.dto.ride.RideCreationDTO;
import rs.ac.uns.ftn.transport.model.Ride;
import rs.ac.uns.ftn.transport.model.Vehicle;
import rs.ac.uns.ftn.transport.service.interfaces.IVehicleTypeService;

@Component
public class RideCreatedDTOMapper {

    private static ModelMapper modelMapper;
    private static IVehicleTypeService vehicleTypeService;
    @Autowired
    public RideCreatedDTOMapper(ModelMapper modelMapper, IVehicleTypeService vehicleTypeService) {

        RideCreatedDTOMapper.modelMapper = modelMapper;
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE)
                .setMatchingStrategy(MatchingStrategies.STANDARD);
        RideCreatedDTOMapper.vehicleTypeService = vehicleTypeService;
    }

    public static RideCreatedDTO fromRideToDTO(Ride model) {
        String vehicleType = model.getVehicleType().getName();
        RideCreatedDTO rideDTO = modelMapper.map(model,RideCreatedDTO.class);
        rideDTO.setVehicleType(vehicleType);
        return rideDTO;
    }

    public static Ride fromDTOtoRide(RideCreatedDTO dto)
    {
        Ride ride = modelMapper.map(dto, Ride.class);
        ride.setVehicleType(vehicleTypeService.findByName(dto.getVehicleType().toUpperCase()));
        return modelMapper.map(dto,Ride.class);
    }
}
