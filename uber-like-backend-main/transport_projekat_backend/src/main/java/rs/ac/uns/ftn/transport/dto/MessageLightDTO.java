package rs.ac.uns.ftn.transport.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rs.ac.uns.ftn.transport.model.Ride;
import rs.ac.uns.ftn.transport.model.User;
import rs.ac.uns.ftn.transport.model.enumerations.MessageType;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageLightDTO {
    private  Integer id;
    private LocalDateTime timeOfSending;
    private  Integer senderId;
    private Integer receiverId;
    private String message;
    private String type;
    private Integer rideId;
}
