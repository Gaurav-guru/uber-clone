package rs.ac.uns.ftn.transport.mapper.ride;

import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.transport.dto.ride.RideCreationDTO;
import rs.ac.uns.ftn.transport.model.Ride;


@Component
public class RideCreationDTOMapper {

    private static ModelMapper modelMapper;
    @Autowired
    public RideCreationDTOMapper(ModelMapper modelMapper) {

        RideCreationDTOMapper.modelMapper = modelMapper;
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE)
                .setMatchingStrategy(MatchingStrategies.STANDARD);

        modelMapper.typeMap(Ride.class, RideCreationDTO.class).addMappings(mapper -> {
            mapper.map(src -> src.getVehicleType().getName(),
                    RideCreationDTO::setVehicleType);
        });

        modelMapper.typeMap(RideCreationDTO.class, Ride.class).addMappings(mapper -> {
            mapper.map(src -> src.getVehicleType(),
                    Ride::setVehicleTypeByName);
        });
    }

    public static RideCreationDTO fromRideToDTO(Ride model) { return modelMapper.map(model, RideCreationDTO.class); }

    public static Ride fromDTOtoRide(RideCreationDTO dto) { return modelMapper.map(dto,Ride.class); }
}
