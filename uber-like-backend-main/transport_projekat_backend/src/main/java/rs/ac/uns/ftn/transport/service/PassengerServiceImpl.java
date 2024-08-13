package rs.ac.uns.ftn.transport.service;

import javax.mail.MessagingException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import rs.ac.uns.ftn.transport.dto.passenger.PassengerOneDayReportDTO;
import rs.ac.uns.ftn.transport.model.Passenger;
import rs.ac.uns.ftn.transport.model.Ride;
import rs.ac.uns.ftn.transport.model.UserActivation;
import rs.ac.uns.ftn.transport.repository.PassengerRepository;
import rs.ac.uns.ftn.transport.repository.RideRepository;
import rs.ac.uns.ftn.transport.service.interfaces.IPassengerService;
import org.springframework.data.domain.Pageable;
import rs.ac.uns.ftn.transport.service.interfaces.IRideService;
import rs.ac.uns.ftn.transport.service.interfaces.IRoleService;
import rs.ac.uns.ftn.transport.service.interfaces.IUserActivationService;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class PassengerServiceImpl implements IPassengerService {

    private final PassengerRepository passengerRepository;
    private final RideRepository rideRepository;
    private final IRideService rideService;
    private final IUserActivationService activationService;
    private final IRoleService roleService;

    public PassengerServiceImpl(PassengerRepository passengerRepository,
                                RideRepository rideRepository,
                                IRideService rideService,
                                IUserActivationService activationService,
                                IRoleService roleService) {
        this.passengerRepository = passengerRepository;
        this.rideRepository = rideRepository;
        this.rideService = rideService;
        this.activationService = activationService;
        this.roleService = roleService;
    }

    private BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public Passenger save(Passenger passenger) throws MessagingException, UnsupportedEncodingException {
        passenger.setIsBlocked(false);
        passenger.setIsActivated(false);
        try {
            BCryptPasswordEncoder passwordEncoder = this.passwordEncoder();
            passenger.setPassword(passwordEncoder.encode(passenger.getPassword()));
            Passenger created = passengerRepository.save(passenger);
            UserActivation activation = new UserActivation(created);
            activationService.save(activation);
            return created;
        }
        catch(DataIntegrityViolationException | MessagingException | UnsupportedEncodingException ex) {
            throw ex;
        }
    }

    @Override
    public Passenger update(Passenger passenger) {
        try{
            return passengerRepository.save(passenger);
        }
        catch(DataIntegrityViolationException ex) {
            throw ex;
        }
    }

    @Override
    public Passenger findOne(Integer id) {
        Optional<Passenger> passenger = passengerRepository.findById(id);
        if(passenger.isEmpty() || !passenger.get().getIsActivated())
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return passenger.get();
    }

    public Page<Passenger> findAll(Pageable page) {
        return passengerRepository.findAll(page);
    }

    @Override
    public List<PassengerOneDayReportDTO> getReport(Integer passengerId, LocalDateTime from, LocalDateTime to) {
        List<PassengerOneDayReportDTO> reports = new ArrayList<>();

        for (LocalDateTime date = from; date.isBefore(to.plusDays(1)); date = date.plusDays(1)) {
            Integer rides = rideRepository.countRidesByPassengerIdAndRideDate(passengerId,
                    date.truncatedTo(ChronoUnit.DAYS),
                    date.plusDays(1).truncatedTo(ChronoUnit.DAYS));

            Double totalDistance = rideRepository.sumDistanceByPassengerIdAndRideDate(passengerId,
                    date.truncatedTo(ChronoUnit.DAYS),
                    date.plusDays(1).truncatedTo(ChronoUnit.DAYS));

            Double totalCost = rideRepository.sumPriceByPassengerIdAndRideDate(passengerId,
                    date.truncatedTo(ChronoUnit.DAYS),
                    date.plusDays(1).truncatedTo(ChronoUnit.DAYS));

            reports.add(new PassengerOneDayReportDTO(rides != null ? rides : 0,
                    totalDistance != null ? totalDistance : 0,
                    totalCost != null ? totalCost : 0));
        }
        return reports;
    }

    @Override
    public Passenger findByEmail(String email) {
        Optional<Passenger> passenger = passengerRepository.findByEmail(email);
        if(passenger.isEmpty() || !passenger.get().getIsActivated()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return passenger.get();
    }

    @Override
    public Page<Ride> findAllByPassenger_Id(Integer id, Pageable page) {
        return rideService.findAllByPassenger_Id(id, page);
    }

    @Override
    public Page<Ride> findAllByPassenger_IdAndStartTimeIsAfterAndEndTimeIsBefore(Integer id, LocalDateTime start, LocalDateTime end, Pageable page) {
        return rideService.findAllByPassenger_IdAndStartTimeIsAfterAndEndTimeIsBefore(id, start, end, page);
    }

    @Override
    public Page<Ride> findAllByPassenger_IdAndStartTimeIsAfter(Integer id, LocalDateTime start, Pageable page) {
        return rideService.findAllByPassenger_IdAndStartTimeIsAfter(id,start,page);
    }

    @Override
    public Page<Ride> findAllByPassenger_IdAndEndTimeIsBefore(Integer id, LocalDateTime end, Pageable page) {
        return rideService.findAllByPassenger_IdAndEndTimeIsBefore(id,end,page);
    }
}
