package rs.ac.uns.ftn.transport.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.transport.dto.LocationDTO;
import rs.ac.uns.ftn.transport.model.Location;

@Component
public class LocationDTOMapper {
    private static ModelMapper modelMapper;

    @Autowired
    public LocationDTOMapper(ModelMapper modelMapper) {
        LocationDTOMapper.modelMapper = modelMapper;
    }

    public static Location fromDTOtoLocation(LocationDTO dto) {
        return modelMapper.map(dto, Location.class);
    }

    public static LocationDTO fromLocationtoDTO(Location dto) {
        return modelMapper.map(dto, LocationDTO.class);
    }
}