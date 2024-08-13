package rs.ac.uns.ftn.transport.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import rs.ac.uns.ftn.transport.model.WorkingHours;

import java.time.LocalDateTime;
import java.util.Set;

public interface WorkingHoursRepository extends JpaRepository<WorkingHours, Integer>, JpaSpecificationExecutor<WorkingHours> {
    Page<WorkingHours> findAllByDriver_Id(@Param("id") Integer id, Pageable page);
    Page<WorkingHours> findAllByDriver_IdAndStartIsAfterAndEndIsBefore(@Param("id") Integer id, @Param("from") LocalDateTime start, @Param("to") LocalDateTime end, Pageable page);
    Page<WorkingHours> findAllByDriver_IdAndStartIsAfter(@Param("id") Integer id, @Param("from") LocalDateTime start, Pageable page);
    Page<WorkingHours> findAllByDriver_IdAndEndIsBefore(@Param("id") Integer id, @Param("to") LocalDateTime end, Pageable page);
    Set<WorkingHours> findAllByDriver_IdAndEndIsAfter(Integer id, LocalDateTime end);
}