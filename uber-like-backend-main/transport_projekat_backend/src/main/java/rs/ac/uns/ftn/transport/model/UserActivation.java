package rs.ac.uns.ftn.transport.model;

import javax.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;


@Entity
@Table(name = "userActivations")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserActivation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne()
    @JoinColumn(name="user_id",referencedColumnName = "id")
    private User user;

    @Column(name = "dateCreated")
    private LocalDateTime dateCreated;

    @Column(name = "minutesValid")
    private Integer minutesValid;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UserActivation that = (UserActivation) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public boolean checkIfExpired() {
        LocalDateTime expiryDate = dateCreated.plus(minutesValid,ChronoUnit.MINUTES);
        if (expiryDate.isBefore(LocalDateTime.now())) return true;
        return false;
    }

    public UserActivation(User user) {
        this.dateCreated = LocalDateTime.now();
        this.minutesValid = 3;
        this.user = user;
    }
}
