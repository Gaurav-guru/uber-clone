package rs.ac.uns.ftn.transport.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import rs.ac.uns.ftn.transport.model.WorkingHours;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;

public interface IWorkingHoursService {
    WorkingHours start(WorkingHours workingHours);
    WorkingHours end(WorkingHours workingHours);
    Duration getDurationWorkedInPastDay(Integer driverId);
    WorkingHours findOne(Integer id);
    Page<WorkingHours> findAllByDriver_Id(Integer id, Pageable page);
    Page<WorkingHours> findAllByDriver_IdAndStartIsAfterAndEndIsBefore(Integer id, LocalDateTime start, LocalDateTime end, Pageable page);
    Page<WorkingHours> findAllByDriver_IdAndStartIsAfter(Integer id, LocalDateTime start, Pageable page);
    Page<WorkingHours> findAllByDriver_IdAndEndIsBefore(Integer id, LocalDateTime end, Pageable page);
}