package rs.ac.uns.ftn.transport.dto;

import lombok.Data;
import rs.ac.uns.ftn.transport.model.enumerations.MessageType;

import java.time.LocalDateTime;

@Data
public class MessageMostSimpleDTO {

    private Integer id;
    private Integer sender;
    private Integer receiver;
    private String message;
    private LocalDateTime sentDateTime;
    private MessageType messageType;
    private Integer ride;
}
