package rs.ac.uns.ftn.transport.mapper.passenger;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.transport.dto.passenger.PassengerDTO;
import rs.ac.uns.ftn.transport.model.Passenger;

@Component
public class PassengerDTOMapper {
    private static ModelMapper modelMapper;

    @Autowired
    public PassengerDTOMapper(ModelMapper modelMapper) {
        PassengerDTOMapper.modelMapper = modelMapper;
    }

    public static Passenger fromDTOtoPassenger(PassengerDTO dto) {
        return modelMapper.map(dto, Passenger.class);
    }

    public static PassengerDTO fromPassengerToDTO(Passenger model) {
        return modelMapper.map(model, PassengerDTO.class);
    }
}
