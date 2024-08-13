package rs.ac.uns.ftn.transport.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import rs.ac.uns.ftn.transport.model.Driver;
import rs.ac.uns.ftn.transport.repository.DriverRepository;
import rs.ac.uns.ftn.transport.specification.WorkingHoursSpecification;
import rs.ac.uns.ftn.transport.model.WorkingHours;
import rs.ac.uns.ftn.transport.repository.WorkingHoursRepository;
import rs.ac.uns.ftn.transport.service.interfaces.IWorkingHoursService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Service
public class WorkingHoursServiceImpl implements IWorkingHoursService {
    private final WorkingHoursRepository workingHoursRepository;
    private final DriverRepository driverRepository;

    public WorkingHoursServiceImpl(WorkingHoursRepository workingHoursRepository, DriverRepository driverRepository) {
        this.workingHoursRepository = workingHoursRepository;
        this.driverRepository = driverRepository;
    }
    public WorkingHours start(WorkingHours workingHours) {
        Optional<WorkingHours> inProgressShift = workingHoursRepository.findOne(WorkingHoursSpecification.startEqualsEnd(workingHours.getDriver().getId()));
        if (inProgressShift.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ongoing");
        }

        Duration totalDuration = getDurationWorkedInPastDay(workingHours.getDriver().getId());

        if (totalDuration.toHours() >= 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "limit");
        }

        Driver driver = workingHours.getDriver();
        driver.setIsActive(true);
        this.driverRepository.save(driver);
        return workingHoursRepository.save(workingHours);
    }

    public Duration getDurationWorkedInPastDay(Integer driverId) {
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
        Set<WorkingHours> shiftsInPastDay = workingHoursRepository.findAllByDriver_IdAndEndIsAfter(driverId, oneDayAgo);

        Duration totalDuration = Duration.ZERO;
        for (WorkingHours shift : shiftsInPastDay) {
            if (shift.getStart().isAfter(oneDayAgo)) {
                totalDuration = totalDuration.plus(Duration.between(shift.getStart(), shift.getEnd()));
            } else {
                totalDuration = totalDuration.plus(Duration.between(oneDayAgo, shift.getEnd()));
            }
            // if the shift is ongoing, add the time since the start of the shift until right now
            if (shift.getStart().equals(shift.getEnd())) {
                totalDuration = totalDuration.plus(Duration.between(shift.getStart(), LocalDateTime.now()));
            }
        }
        return totalDuration;
    }

    public WorkingHours end(WorkingHours workingHours) {

        Driver driver = workingHours.getDriver();
        driver.setIsActive(false);
        this.driverRepository.save(driver);
        return workingHoursRepository.save(workingHours);
    }

    public WorkingHours findOne(Integer id) {
        Optional<WorkingHours> found = workingHoursRepository.findById(id);
        if (found.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return found.get();
    }

    public Page<WorkingHours> findAllByDriver_Id(Integer id, Pageable page) {
        return workingHoursRepository.findAllByDriver_Id(id, page);
    }

    public Page<WorkingHours> findAllByDriver_IdAndStartIsAfterAndEndIsBefore(Integer id, LocalDateTime start, LocalDateTime end, Pageable page) {
        return workingHoursRepository.findAllByDriver_IdAndStartIsAfterAndEndIsBefore(id, start, end, page);
    }

    public Page<WorkingHours> findAllByDriver_IdAndStartIsAfter(Integer id, LocalDateTime start, Pageable page) {
        return workingHoursRepository.findAllByDriver_IdAndStartIsAfter(id, start, page);
    }

    public Page<WorkingHours> findAllByDriver_IdAndEndIsBefore(Integer id, LocalDateTime end, Pageable page) {
        return workingHoursRepository.findAllByDriver_IdAndEndIsBefore(id, end, page);
    }
}