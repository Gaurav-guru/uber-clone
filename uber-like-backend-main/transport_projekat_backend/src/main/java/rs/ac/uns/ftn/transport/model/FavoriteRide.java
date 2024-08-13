package rs.ac.uns.ftn.transport.model;

import javax.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;
import rs.ac.uns.ftn.transport.service.StaticVehicleServiceImpl;

import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "favorites")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteRide {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="FavoriteName")
    private String favoriteName;

    @ManyToMany(cascade = {CascadeType.PERSIST})
    @JoinTable(name = "Favorite_route", joinColumns = @JoinColumn(name = "FavoriteId", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "RouteId", referencedColumnName = "id"))
    @ToString.Exclude
    private Set<Route> locations;

    @ManyToMany(fetch = FetchType.EAGER)
    @ToString.Exclude
    private Set<Passenger> passengers;

    @Column(name = "TransportsBaby")
    private Boolean babyTransport;

    @Column(name = "TransportsPet")
    private Boolean petTransport;

    @Column(name = "rideId")
    private Integer rideId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VehicleType")
    @ToString.Exclude
    private VehicleType vehicleType;

    public void setVehicleTypeByName(String name)
    {
        VehicleType byName = StaticVehicleServiceImpl.findByName(name);
        this.vehicleType = byName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        FavoriteRide ride = (FavoriteRide) o;
        return id != null && Objects.equals(id, ride.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
