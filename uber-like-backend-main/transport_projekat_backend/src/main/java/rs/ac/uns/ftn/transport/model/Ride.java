package rs.ac.uns.ftn.transport.model;

import javax.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;
import rs.ac.uns.ftn.transport.model.enumerations.RideStatus;
import rs.ac.uns.ftn.transport.service.StaticVehicleServiceImpl;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "rides")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "StartTime")
    private LocalDateTime startTime;

    @Column (name = "EndTime")
    private LocalDateTime endTime;

    @Column (name = "TotalPrice")
    private Double totalCost;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn (name = "DriverId")
    @ToString.Exclude
    private Driver driver;

    @ManyToMany(cascade = {CascadeType.MERGE},fetch = FetchType.EAGER)
    @ToString.Exclude
    private Set<Passenger> passengers;

    @ManyToMany(cascade = {CascadeType.PERSIST},fetch = FetchType.EAGER)
    @JoinTable(name = "Ride_route", joinColumns = @JoinColumn(name = "RideId", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "RouteId", referencedColumnName = "id"))
    @ToString.Exclude
    private Set<Route> locations;

    @Column(name = "EstimatedTimeInMinutes")
    private Integer estimatedTimeInMinutes;

    @OneToMany(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<Review> reviews;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status")
    private RideStatus status;

    @OneToOne(mappedBy = "ride", fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST})
    @ToString.Exclude
    private Rejection rejection;

    @Column(name = "IsPanicPressed")
    private Boolean isPanicPressed;

    @Column(name = "TransportsBaby")
    private Boolean babyTransport;

    @Column(name = "TransportsPet")
    private Boolean petTransport;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VehicleType")
    @ToString.Exclude
    private VehicleType vehicleType;

    @Column(name="ScheduledTime")
    private LocalDateTime scheduledTime;
    

    public void setVehicleTypeByName(String name)
    {
        VehicleType byName = StaticVehicleServiceImpl.findByName(name);
        this.vehicleType = byName;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Ride ride = (Ride) o;
        return id != null && Objects.equals(id, ride.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
