package rs.ac.uns.ftn.transport.mapper.ride;

import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.transport.dto.ride.FavoriteRideDTO;
import rs.ac.uns.ftn.transport.dto.ride.RideCreatedDTO;
import rs.ac.uns.ftn.transport.model.FavoriteRide;
import rs.ac.uns.ftn.transport.model.Ride;
import rs.ac.uns.ftn.transport.service.interfaces.IVehicleTypeService;

@Component
public class FavoriteRideDTOMapper {
    private static ModelMapper modelMapper;
    private static IVehicleTypeService vehicleTypeService;
    @Autowired
    public FavoriteRideDTOMapper(ModelMapper modelMapper, IVehicleTypeService vehicleTypeService) {

        FavoriteRideDTOMapper.modelMapper = modelMapper;
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE)
                .setMatchingStrategy(MatchingStrategies.STANDARD);
        FavoriteRideDTOMapper.vehicleTypeService = vehicleTypeService;
    }

    public static FavoriteRideDTO fromFavoriteRideToDTO(FavoriteRide model) {
        String vehicleType = model.getVehicleType().getName();
        FavoriteRideDTO rideDTO = modelMapper.map(model,FavoriteRideDTO.class);
        rideDTO.setVehicleType(vehicleType);
        return rideDTO;
    }

    public static FavoriteRide fromDTOtoFavoriteRide(FavoriteRideDTO dto)
    {
        FavoriteRide ride = modelMapper.map(dto, FavoriteRide.class);
        ride.setVehicleType(vehicleTypeService.findByName(dto.getVehicleType().toUpperCase()));
        return ride;
    }
}
