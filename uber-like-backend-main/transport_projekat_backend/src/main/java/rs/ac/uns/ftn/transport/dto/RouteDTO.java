package rs.ac.uns.ftn.transport.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RouteDTO {

    @NotNull(message="{required}")
    @Valid
    private LocationDTO departure;

    @NotNull(message = "{required}")
    @Valid
    private LocationDTO destination;
}
