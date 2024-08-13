package rs.ac.uns.ftn.transport.model;

import javax.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.Objects;

import static javax.persistence.InheritanceType.*;

//TODO: Model nije jasan sto se tice recenzija (vozac i vozilo odvojeno). Pitacu asistenta za ovo. Do tada ostaje prazno
@Entity
@Table(name = "reviews")
@Inheritance(strategy = JOINED)
@Getter
@Setter
@ToString
public abstract class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reviewerId")
    @ToString.Exclude
    private User reviewer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currentRide")
    @ToString.Exclude
    private Ride currentRide;

    @Column(name = "comment")
    private String comment;

    @Column(name = "rating")
    private Integer rating;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Review review = (Review) o;
        return id != null && Objects.equals(id, review.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
