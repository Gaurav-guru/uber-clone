package rs.ac.uns.ftn.transport.service;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import rs.ac.uns.ftn.transport.model.FavoriteRide;
import rs.ac.uns.ftn.transport.model.Passenger;
import rs.ac.uns.ftn.transport.repository.FavoriteRideRepository;
import rs.ac.uns.ftn.transport.repository.PassengerRepository;
import rs.ac.uns.ftn.transport.repository.RideRepository;
import rs.ac.uns.ftn.transport.service.interfaces.IFavoriteRideService;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
public class FavoriteRideServiceImpl implements IFavoriteRideService {

    private final MessageSource messageSource;
    private final FavoriteRideRepository favRideRepository;
    private final PassengerRepository passengerRepository;
    private final RideRepository rideRepository;

    public FavoriteRideServiceImpl(FavoriteRideRepository favRideRepository,
                                   MessageSource messageSource, PassengerRepository passengerRepository,
                                   RideRepository rideRepository) {
        this.favRideRepository = favRideRepository;
        this.messageSource = messageSource;
        this.passengerRepository = passengerRepository;
        this.rideRepository = rideRepository;
    }


    @Override
    public FavoriteRide save(FavoriteRide ride) {
        //TODO Get user from token and get count of his favorites
        Passenger passenger = this.passengerRepository.findById(1).get();
        long favoritesCount = passenger.getFavorites().size();
        if(favoritesCount >= 10) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("favorites.maxLength", null, Locale.getDefault()));
        }
        FavoriteRide saved = this.favRideRepository.save(ride);
        passenger.getFavorites().add(saved);
        this.passengerRepository.save(passenger);
        return saved;
    }

    @Override
    public void delete(Integer id) {
        Optional<FavoriteRide> favorite = this.favRideRepository.findById(id);
        if(favorite.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,messageSource.getMessage("favorite.notFound", null, Locale.getDefault()));
        }
        Passenger passenger = this.passengerRepository.findById(1).get();
        passenger.getFavorites().removeIf(fav -> fav.getId() == id);
        this.passengerRepository.save(passenger);
        favorite.get().setPassengers(null);
        this.favRideRepository.save(favorite.get());
        this.favRideRepository.deleteById(id);
    }

    @Override
    public Set<FavoriteRide> findAll() {

        //TODO Get user from token and get his favorites
        Set<FavoriteRide> favorites = this.passengerRepository.findById(1).get().getFavorites();
        return favorites;
    }

    @Override
    public Set<FavoriteRide> findAllByPassenger(Integer id) {
        return favRideRepository.findAllByPassengerId(id);
    }
}
