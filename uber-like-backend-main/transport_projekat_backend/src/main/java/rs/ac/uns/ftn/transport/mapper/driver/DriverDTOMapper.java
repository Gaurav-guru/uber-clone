package rs.ac.uns.ftn.transport.mapper.driver;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.transport.dto.driver.DriverDTO;
import rs.ac.uns.ftn.transport.model.Driver;

@Component
public class DriverDTOMapper {
    private static ModelMapper modelMapper;

    @Autowired
    public DriverDTOMapper(ModelMapper modelMapper) {
        DriverDTOMapper.modelMapper = modelMapper;
    }

    public static Driver fromDTOtoDriver(DriverDTO dto) {
        return modelMapper.map(dto, Driver.class);
    }

    public static DriverDTO fromDrivertoDTO(Driver dto) {
        return modelMapper.map(dto, DriverDTO.class);
    }
}