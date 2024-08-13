package rs.ac.uns.ftn.transport.model;


import javax.persistence.*;

import com.google.common.base.Strings;
import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.security.core.GrantedAuthority;
import rs.ac.uns.ftn.transport.dto.passenger.PassengerWithoutIdPasswordDTO;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "passengers")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Passenger extends User{

    @ManyToMany()
    @JoinTable(name = "passenger_Ride", joinColumns = @JoinColumn(name = "PassengerId", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "RideId", referencedColumnName = "id"))
    @ToString.Exclude
    private Set<Ride> rides;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "passenger_FavoriteRide", joinColumns = @JoinColumn(name = "PassengerId", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "FavoriteRideId", referencedColumnName = "id"))
    @ToString.Exclude
    private Set<FavoriteRide> favorites;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Passenger passenger = (Passenger) o;
        return getId() != null && Objects.equals(getId(), passenger.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public void update(PassengerWithoutIdPasswordDTO newInfo)
    {
        this.setName(newInfo.getName());
        this.setSurname(newInfo.getSurname());
        if (!Strings.isNullOrEmpty(newInfo.getProfilePicture())) {
            this.setProfilePicture(newInfo.getProfilePicture());
        }
        this.setAddress(newInfo.getAddress());
        if (!Strings.isNullOrEmpty(newInfo.getTelephoneNumber())) {
            this.setTelephoneNumber(newInfo.getTelephoneNumber());
        }
        this.setEmail(newInfo.getEmail());
    }
}
