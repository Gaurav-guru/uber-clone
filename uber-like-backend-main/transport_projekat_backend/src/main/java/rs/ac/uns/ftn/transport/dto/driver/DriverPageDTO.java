package rs.ac.uns.ftn.transport.dto.driver;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverPageDTO {
    public Long totalCount;
    public Set<DriverDTO> results;
}