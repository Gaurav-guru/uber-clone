package rs.ac.uns.ftn.transport.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.transport.dto.MessageDTO;
import rs.ac.uns.ftn.transport.dto.PanicDTO;
import rs.ac.uns.ftn.transport.model.Message;
import rs.ac.uns.ftn.transport.model.Panic;

@Component
public class MessageDTOMapper {
    private static ModelMapper modelMapper;

    @Autowired
    public MessageDTOMapper(ModelMapper modelMapper){MessageDTOMapper.modelMapper = modelMapper;}

    public static Message fromDTOtoMessage(MessageDTO dto) {return modelMapper.map(dto,  Message.class);}

    public static MessageDTO fromMessagetoDTO(Message dto) {return modelMapper.map(dto, MessageDTO.class);}
}
