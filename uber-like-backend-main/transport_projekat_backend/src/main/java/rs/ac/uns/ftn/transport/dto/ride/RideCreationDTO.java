package rs.ac.uns.ftn.transport.dto.ride;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import rs.ac.uns.ftn.transport.dto.passenger.PassengerIdEmailDTO;
import rs.ac.uns.ftn.transport.dto.RouteDTO;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideCreationDTO {

    @Size(min = 1, message = "{minLength}")
    @Valid
    private Set<RouteDTO> locations;

    @Size(min=1, message = "{minLength}")
    @Valid
    private Set<PassengerIdEmailDTO> passengers;

    @Length(max=50,message = "{maxLength}")
    @NotBlank(message = "{required}")
    private String vehicleType;
    @NotNull(message="{required}")
    private Boolean babyTransport;
    @NotNull(message="{required}")
    private Boolean petTransport;
    @Valid
    private LocalDateTime scheduledTime;

}
