package rs.ac.uns.ftn.transport.service.interfaces;

import javax.mail.MessagingException;
import org.springframework.data.domain.Page;
import rs.ac.uns.ftn.transport.dto.passenger.PassengerOneDayReportDTO;
import rs.ac.uns.ftn.transport.model.Passenger;
import org.springframework.data.domain.Pageable;
import rs.ac.uns.ftn.transport.model.Ride;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface IPassengerService {

    Passenger save(Passenger passenger) throws MessagingException, UnsupportedEncodingException;
    Passenger update(Passenger passenger);
    Passenger findOne(Integer id);
    Page<Passenger> findAll(Pageable page);
    Passenger findByEmail(String email);
    Page<Ride> findAllByPassenger_Id(Integer id, Pageable page);
    Page<Ride> findAllByPassenger_IdAndStartTimeIsAfterAndEndTimeIsBefore(Integer id, LocalDateTime start, LocalDateTime end, Pageable page);
    Page<Ride> findAllByPassenger_IdAndStartTimeIsAfter(Integer id, LocalDateTime start, Pageable page);
    Page<Ride> findAllByPassenger_IdAndEndTimeIsBefore(Integer id, LocalDateTime end, Pageable page);
    List<PassengerOneDayReportDTO> getReport(Integer passengerId, LocalDateTime from, LocalDateTime to);
}
