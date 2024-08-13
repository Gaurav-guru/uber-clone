package rs.ac.uns.ftn.transport.mapper.driver;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.transport.dto.driver.DriverDTO;
import rs.ac.uns.ftn.transport.dto.driver.DriverPasswordDTO;
import rs.ac.uns.ftn.transport.model.Driver;

@Component
public class DriverPasswordDTOMapper {
    private static ModelMapper modelMapper;

    @Autowired
    public DriverPasswordDTOMapper(ModelMapper modelMapper) {
        DriverPasswordDTOMapper.modelMapper = modelMapper;
    }

    public static Driver fromDTOtoDriver(DriverPasswordDTO dto) {
        return modelMapper.map(dto, Driver.class);
    }

    public static DriverPasswordDTO fromDrivertoDTO(Driver dto) {
        return modelMapper.map(dto, DriverPasswordDTO.class);
    }
}