package rs.ac.uns.ftn.transport.model;

import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "admins")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Admin extends User {

}
