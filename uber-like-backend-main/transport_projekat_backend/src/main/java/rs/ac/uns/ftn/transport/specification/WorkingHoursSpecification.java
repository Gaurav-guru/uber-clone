package rs.ac.uns.ftn.transport.specification;

import org.springframework.data.jpa.domain.Specification;
import rs.ac.uns.ftn.transport.model.WorkingHours;

public class WorkingHoursSpecification {
    public static Specification<WorkingHours> startEqualsEnd(Integer driverId) {
        return (root, query, cb) ->
                cb.and(cb.equal(root.get("start"), root.get("end")), cb.equal(root.get("driver").get("id"), driverId));
    }
}