package rs.ac.uns.ftn.transport.dto.workinghours;

import javax.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WorkingHoursEndDTO {
    @NotNull
    private LocalDateTime end;
}