package rs.ac.uns.ftn.transport.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rs.ac.uns.ftn.transport.model.Message;
import rs.ac.uns.ftn.transport.model.Ride;
import rs.ac.uns.ftn.transport.model.User;
import rs.ac.uns.ftn.transport.model.enumerations.MessageType;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private Integer id;
    private User sender;
    private User receiver;
    private String message;
    private LocalDateTime sentDateTime;
    private MessageType messageType;
    private Ride ride;

    public MessageDTO(Message message) {
        this.id = message.getId();
        this.sender = message.getSender();
        this.receiver = message.getReceiver();
        this.message = message.getMessage();
        this.sentDateTime = message.getSentDateTime();
        this.messageType = message.getMessageType();
        this.ride = message.getRide();
    }
}
