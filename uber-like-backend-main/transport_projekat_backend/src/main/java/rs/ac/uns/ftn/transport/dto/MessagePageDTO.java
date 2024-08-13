package rs.ac.uns.ftn.transport.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rs.ac.uns.ftn.transport.model.Message;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessagePageDTO {
    public Long totalCount;
    public Set<MessageDTO> results;
}
