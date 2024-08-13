package rs.ac.uns.ftn.transport.model;

import javax.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "rejections")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Rejection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rideId")
    @ToString.Exclude
    private Ride ride;

    @Column(name = "reason")
    private String reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    @ToString.Exclude
    private User user;

    @Column(name = "dateTime")
    private LocalDateTime timeOfRejection;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Rejection rejection = (Rejection) o;
        return id != null && Objects.equals(id, rejection.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
