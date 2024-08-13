package rs.ac.uns.ftn.transport.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.transport.dto.PanicDTO;
import rs.ac.uns.ftn.transport.model.Panic;

@Component
public class PanicDTOMapper {
    private static ModelMapper modelMapper;

    @Autowired
    public PanicDTOMapper(ModelMapper modelMapper){PanicDTOMapper.modelMapper = modelMapper;}

    public static Panic fromDTOtoPanic(PanicDTO dto) {return modelMapper.map(dto,  Panic.class);}

    public static PanicDTO fromPanictoDTO(Panic dto) {return modelMapper.map(dto, PanicDTO.class);}
}
