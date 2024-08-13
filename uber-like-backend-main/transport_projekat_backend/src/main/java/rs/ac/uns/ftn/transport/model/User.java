package rs.ac.uns.ftn.transport.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static javax.persistence.InheritanceType.*;

@Entity
@Table(name = "users")
@Inheritance(strategy = JOINED)
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public abstract class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", unique=true, nullable=false)
    private Integer id;

    @Column (name = "name", nullable = false)
    private String name;

    @Column (name = "surname", nullable = false)
    private String surname;

    @Column (name = "profilePicture")
    @Lob
    private String profilePicture;

    @Column (name = "telephoneNumber")
    private String telephoneNumber;

    @Column (name = "email", nullable = false, unique = true)
    private String email;

    @Column (name = "address", nullable = false)
    private String address;

    @Column (name = "password", nullable = false)
    private String password;

    @Column (name = "isActivated")
    private Boolean isActivated;

    @Column (name = "isBlocked")
    private Boolean isBlocked;

    @Column (name = "resetPasswordToken")
    private String resetPasswordToken;

    @Column (name = "resetPasswordTokenExpiration")
    private LocalDateTime resetPasswordTokenExpiration;

    @Column (name = "lastPasswordResetDate")
    private Date lastPasswordResetDate;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private List<Role> roles;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return id != null && Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }

    public User(Integer id) {
        this.id = id;
    }

    public User(Integer id, Boolean isBlocked) {
        this.id = id;
        this.isBlocked = isBlocked;
    }
}
