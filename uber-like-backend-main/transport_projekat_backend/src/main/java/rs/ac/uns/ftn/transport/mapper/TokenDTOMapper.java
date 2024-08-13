package rs.ac.uns.ftn.transport.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.transport.dto.TokenDTO;
import rs.ac.uns.ftn.transport.model.Token;

@Component
public class TokenDTOMapper {
    private static ModelMapper modelMapper;

    @Autowired
    public TokenDTOMapper(ModelMapper modelMapper){TokenDTOMapper.modelMapper = modelMapper;}

    public static Token fromDTOtoToken(TokenDTO dto) {return modelMapper.map(dto,  Token.class);}

    public static TokenDTO fromTokentoDTO(Token dto) {return modelMapper.map(dto, TokenDTO.class);}
}
