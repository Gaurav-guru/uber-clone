package rs.ac.uns.ftn.transport.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.Objects;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(mappedBy = "vehicle")
    private Driver driver;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicleType")
    @ToString.Exclude
    @JsonIgnore
    private VehicleType vehicleType;

    @Column(name = "model")
    private String model;

    @Column(name = "licenseNumber")
    private String licenseNumber;

    @OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "currentLocation")
    @ToString.Exclude
    @JsonIgnore
    private Location currentLocation;

    @Column(name = "passengerSeats")
    private Integer passengerSeats;

    @Column(name = "babyTransport")
    private Boolean babyTransport;

    @Column(name = "petTransport")
    private Boolean petTransport;

    // TODO: vratiti kada review bude spreman
//    @OneToMany(mappedBy = "vehicle",fetch = FetchType.LAZY)
//    Set<Review> reviews;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Vehicle vehicle = (Vehicle) o;
        return id != null && Objects.equals(id, vehicle.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
