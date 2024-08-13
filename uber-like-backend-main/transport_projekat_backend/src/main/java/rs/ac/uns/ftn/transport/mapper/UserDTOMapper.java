package rs.ac.uns.ftn.transport.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.transport.dto.RideDTO;
import rs.ac.uns.ftn.transport.dto.UserDTO;
import rs.ac.uns.ftn.transport.model.Ride;
import rs.ac.uns.ftn.transport.model.User;

@Component
public class UserDTOMapper {
    private static ModelMapper modelMapper;

    @Autowired
    public UserDTOMapper(ModelMapper modelMapper) {
        UserDTOMapper.modelMapper = modelMapper;
    }

    public static User fromDTOtoUser(UserDTO dto) {
        return modelMapper.map(dto, User.class);
    }

    public static UserDTO fromUsertoDTO(User dto) {return modelMapper.map(dto, UserDTO.class);}
}
