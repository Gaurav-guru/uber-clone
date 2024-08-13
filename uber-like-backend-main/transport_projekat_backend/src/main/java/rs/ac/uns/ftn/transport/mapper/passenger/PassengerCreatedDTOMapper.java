package rs.ac.uns.ftn.transport.mapper.passenger;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.transport.dto.passenger.PassengerCreatedDTO;
import rs.ac.uns.ftn.transport.model.Passenger;

@Component
public class PassengerCreatedDTOMapper {
    private static ModelMapper modelMapper;

    @Autowired
    public PassengerCreatedDTOMapper(ModelMapper modelMapper) {
        PassengerCreatedDTOMapper.modelMapper = modelMapper;
    }

    public static Passenger fromDTOtoPassenger(PassengerCreatedDTO dto) {
        return modelMapper.map(dto, Passenger.class);
    }

    public static PassengerCreatedDTO fromPassengerToDTO(Passenger model) {
        return modelMapper.map(model, PassengerCreatedDTO.class);
    }
}
