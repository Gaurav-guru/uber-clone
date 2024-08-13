package rs.ac.uns.ftn.transport.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.transport.dto.DocumentDTO;
import rs.ac.uns.ftn.transport.model.Document;

@Component
public class DocumentDTOMapper {
    private static ModelMapper modelMapper;

    @Autowired
    public DocumentDTOMapper(ModelMapper modelMapper) {
        DocumentDTOMapper.modelMapper = modelMapper;
    }

    public static Document fromDTOtoDocument(DocumentDTO dto) {
        return modelMapper.map(dto, Document.class);
    }

    public static DocumentDTO fromDocumenttoDTO(Document dto) {
        return modelMapper.map(dto, DocumentDTO.class);
    }
}