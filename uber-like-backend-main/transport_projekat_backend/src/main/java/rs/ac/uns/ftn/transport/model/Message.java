package rs.ac.uns.ftn.transport.model;

import javax.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;
import rs.ac.uns.ftn.transport.dto.MessageLightDTO;
import rs.ac.uns.ftn.transport.model.enumerations.MessageType;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;

@Entity
@Table(name = "messages")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "senderId")
    @ToString.Exclude
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiverId")
    @ToString.Exclude
    private User receiver;

    @Column(name = "message")
    private String message;

    @Column(name = "sentDateTime", columnDefinition = "TIMESTAMP")
    private LocalDateTime sentDateTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "messageType")
    private MessageType messageType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rideId")
    @ToString.Exclude
    private Ride ride;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Message message = (Message) o;
        return id != null && Objects.equals(id, message.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
