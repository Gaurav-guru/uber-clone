package rs.ac.uns.ftn.transport.mapper.review;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.transport.dto.passenger.PassengerIdEmailDTO;
import rs.ac.uns.ftn.transport.dto.review.DriverReviewDTO;
import rs.ac.uns.ftn.transport.dto.review.VehicleReviewDTO;
import rs.ac.uns.ftn.transport.model.DriverReview;
import rs.ac.uns.ftn.transport.model.VehicleReview;

@Component
public class VehicleReviewDTOMapper {

    private static ModelMapper modelMapper;

    @Autowired
    public VehicleReviewDTOMapper(ModelMapper modelMapper) {
        VehicleReviewDTOMapper.modelMapper = modelMapper;
        modelMapper.typeMap(VehicleReview.class, VehicleReviewDTO.class).addMappings(mapper -> {
            mapper.map(src -> new PassengerIdEmailDTO(src.getReviewer().getId(), src.getReviewer().getEmail()),
                    VehicleReviewDTO::setReviewer);
        });
    }

    public static VehicleReview fromDTOtoVehicleReview(VehicleReviewDTO dto) {
        return modelMapper.map(dto, VehicleReview.class);
    }

    public static VehicleReviewDTO fromVehicleReviewtoDTO(VehicleReview dto) {
        return modelMapper.map(dto, VehicleReviewDTO.class);
    }
}
