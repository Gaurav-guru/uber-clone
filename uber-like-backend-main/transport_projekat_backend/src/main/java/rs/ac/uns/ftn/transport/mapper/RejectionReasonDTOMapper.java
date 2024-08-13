package rs.ac.uns.ftn.transport.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.transport.dto.RejectionReasonDTO;
import rs.ac.uns.ftn.transport.model.Rejection;

@Component
public class RejectionReasonDTOMapper {

    private static ModelMapper modelMapper;

    @Autowired
    public RejectionReasonDTOMapper(ModelMapper modelMapper){this.modelMapper = modelMapper;}

    public static Rejection fromDTOtoRejection(RejectionReasonDTO dto) {return modelMapper.map(dto,  Rejection.class);}

    public static RejectionReasonDTO fromRejectionToDTO(RejectionReasonDTO model) {return modelMapper.map(model, RejectionReasonDTO.class);}
}
