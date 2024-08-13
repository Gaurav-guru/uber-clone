package rs.ac.uns.ftn.transport.mapper;

import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.transport.dto.RouteDTO;
import rs.ac.uns.ftn.transport.dto.ride.RideCreationDTO;
import rs.ac.uns.ftn.transport.mapper.ride.RideCreationDTOMapper;
import rs.ac.uns.ftn.transport.model.Ride;
import rs.ac.uns.ftn.transport.model.Route;

@Component
public class RouteDTOMapper {

    private static ModelMapper modelMapper;
    @Autowired
    public RouteDTOMapper(ModelMapper modelMapper) {

        RouteDTOMapper.modelMapper = modelMapper;
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE)
                .setMatchingStrategy(MatchingStrategies.STANDARD);

    }

    public static RouteDTO fromRouteToDTO(Route model) { return modelMapper.map(model, RouteDTO.class); }

    public static Route fromDTOtoRoute(RouteDTO dto) { return modelMapper.map(dto,Route.class); }
}
