package rs.ac.uns.ftn.transport.dto;

import javax.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationDTO {

    @Length(max=255,message="{maxLength}")
    private String address;

    @DecimalMin(value= "-90.0",message="{latitude.minLimit}")
    @DecimalMax(value="90.0",message = "{latitude.maxLimit}")
    @NotNull
    private Double latitude;
    @DecimalMin(value= "-180.0",message="{longitude.minLimit}")
    @DecimalMax(value="180.0",message = "{longitude.maxLimit}")
    @NotNull
    private Double longitude;
}