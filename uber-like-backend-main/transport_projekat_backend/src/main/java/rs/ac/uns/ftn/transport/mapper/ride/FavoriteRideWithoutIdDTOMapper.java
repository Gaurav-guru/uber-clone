package rs.ac.uns.ftn.transport.mapper.ride;

import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.transport.dto.RouteDTO;
import rs.ac.uns.ftn.transport.dto.passenger.PassengerIdEmailDTO;
import rs.ac.uns.ftn.transport.dto.ride.FavoriteRideWithoutIdDTO;
import rs.ac.uns.ftn.transport.mapper.RouteDTOMapper;
import rs.ac.uns.ftn.transport.mapper.passenger.PassengerIdEmailDTOMapper;
import rs.ac.uns.ftn.transport.model.FavoriteRide;
import rs.ac.uns.ftn.transport.model.Passenger;
import rs.ac.uns.ftn.transport.repository.PassengerRepository;

import java.util.HashSet;

@Component
public class FavoriteRideWithoutIdDTOMapper {

    private static ModelMapper modelMapper;
    private static PassengerRepository passengerRepository;
    @Autowired
    public FavoriteRideWithoutIdDTOMapper(ModelMapper modelMapper, PassengerRepository passengerRepository) {

        FavoriteRideWithoutIdDTOMapper.modelMapper = modelMapper;
        FavoriteRideWithoutIdDTOMapper.passengerRepository = passengerRepository;
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE)
                .setMatchingStrategy(MatchingStrategies.STANDARD);

        modelMapper.typeMap(FavoriteRide.class, FavoriteRideWithoutIdDTO.class).addMappings(mapper -> {
            mapper.map(src -> src.getVehicleType().getName(),
                    FavoriteRideWithoutIdDTO::setVehicleType);
        });

        modelMapper.typeMap(FavoriteRideWithoutIdDTO.class, FavoriteRide.class).addMappings(mapper -> {
            mapper.map(src -> src.getVehicleType(),
                    FavoriteRide::setVehicleTypeByName);
        });
    }

    public static FavoriteRideWithoutIdDTO fromRideToDTO(FavoriteRide model) {
        return modelMapper.map(model, FavoriteRideWithoutIdDTO.class);
    }

    public static FavoriteRide fromDTOtoFavoriteRide(FavoriteRideWithoutIdDTO dto) {
       FavoriteRide favorite = new FavoriteRide();
        favorite.setVehicleTypeByName(dto.getVehicleType());
        favorite.setFavoriteName(dto.getFavoriteName());
        favorite.setPetTransport(dto.getPetTransport());
        favorite.setBabyTransport(dto.getBabyTransport());
        favorite.setPassengers(new HashSet<>());
        favorite.setLocations(new HashSet<>());
        for(PassengerIdEmailDTO passenger : dto.getPassengers()){
            Passenger found = passengerRepository.findById(passenger.getId()).get();
            favorite.getPassengers().add(found);
        }
        for(RouteDTO route : dto.getLocations()){
            favorite.getLocations().add(RouteDTOMapper.fromDTOtoRoute(route));
        }
        return favorite;
    }
}
