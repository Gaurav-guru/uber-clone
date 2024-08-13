package rs.ac.uns.ftn.transport.dto.workinghours;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkingHoursPageDTO {
    public Long totalCount;
    public Set<WorkingHoursDTO> results;
}