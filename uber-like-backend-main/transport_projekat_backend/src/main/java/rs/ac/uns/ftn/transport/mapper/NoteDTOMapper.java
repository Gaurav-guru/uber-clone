package rs.ac.uns.ftn.transport.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.transport.dto.NoteDTO;
import rs.ac.uns.ftn.transport.model.Note;

@Component
public class NoteDTOMapper {
    private static ModelMapper modelMapper;

    @Autowired
    public NoteDTOMapper(ModelMapper modelMapper){NoteDTOMapper.modelMapper = modelMapper;}

    public static Note fromDTOtoNote(NoteDTO dto) {return modelMapper.map(dto,  Note.class);}

    public static NoteDTO fromNotetoDTO(Note dto) {return modelMapper.map(dto, NoteDTO.class);}
}
