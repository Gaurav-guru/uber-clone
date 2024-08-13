package rs.ac.uns.ftn.transport.dto.review;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rs.ac.uns.ftn.transport.dto.review.DriverReviewDTO;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverReviewPageDTO {
    public Long totalCount;
    public Set<DriverReviewDTO> results;
}
