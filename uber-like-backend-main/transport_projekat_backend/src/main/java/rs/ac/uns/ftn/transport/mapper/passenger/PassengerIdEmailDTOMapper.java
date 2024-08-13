package rs.ac.uns.ftn.transport.mapper.passenger;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.transport.dto.passenger.PassengerDTO;
import rs.ac.uns.ftn.transport.dto.passenger.PassengerIdEmailDTO;
import rs.ac.uns.ftn.transport.model.Passenger;

@Component
public class PassengerIdEmailDTOMapper {
    private static ModelMapper modelMapper;

    @Autowired
    public PassengerIdEmailDTOMapper(ModelMapper modelMapper) {
        PassengerIdEmailDTOMapper.modelMapper = modelMapper;
    }

    public static Passenger fromDTOtoPassenger(PassengerIdEmailDTO dto) {
        return modelMapper.map(dto, Passenger.class);
    }

    public static PassengerIdEmailDTO fromPassengerToDTO(Passenger model) {
        return modelMapper.map(model, PassengerIdEmailDTO.class);
    }
}
