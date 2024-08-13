package rs.ac.uns.ftn.transport.mapper.passenger;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.transport.dto.passenger.PassengerCreatedDTO;
import rs.ac.uns.ftn.transport.dto.passenger.PassengerWithoutIdPasswordDTO;
import rs.ac.uns.ftn.transport.model.Passenger;

@Component
public class PassengerWithoutIdPasswordDTOMapper {
    private static ModelMapper modelMapper;

    @Autowired
    public PassengerWithoutIdPasswordDTOMapper(ModelMapper modelMapper) {
        PassengerWithoutIdPasswordDTOMapper.modelMapper = modelMapper;
    }

    public static Passenger fromDTOtoPassenger(PassengerWithoutIdPasswordDTO dto) {
        return modelMapper.map(dto, Passenger.class);
    }

    public static PassengerWithoutIdPasswordDTO fromPassengerToDTO(Passenger model) {
        return modelMapper.map(model, PassengerWithoutIdPasswordDTO.class);
    }
}
