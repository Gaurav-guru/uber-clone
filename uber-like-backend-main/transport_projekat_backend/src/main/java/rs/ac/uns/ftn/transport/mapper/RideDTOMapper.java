package rs.ac.uns.ftn.transport.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import rs.ac.uns.ftn.transport.dto.RideDTO;
import rs.ac.uns.ftn.transport.model.Ride;

public class RideDTOMapper {

    private static ModelMapper modelMapper;

    @Autowired
    public RideDTOMapper(ModelMapper modelMapper) {
        RideDTOMapper.modelMapper = modelMapper;
    }

    public static Ride fromDTOtoRide(RideDTO dto) {
        return modelMapper.map(dto, Ride.class);
    }

    public static RideDTO fromRidetoDTO(Ride dto) {
        return modelMapper.map(dto, RideDTO.class);
    }
}
