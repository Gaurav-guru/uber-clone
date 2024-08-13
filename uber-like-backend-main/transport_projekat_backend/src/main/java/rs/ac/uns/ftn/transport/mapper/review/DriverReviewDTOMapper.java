package rs.ac.uns.ftn.transport.mapper.review;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.transport.dto.passenger.PassengerIdEmailDTO;
import rs.ac.uns.ftn.transport.dto.review.DriverReviewDTO;
import rs.ac.uns.ftn.transport.model.DriverReview;

@Component
public class DriverReviewDTOMapper {

    private static ModelMapper modelMapper;

    @Autowired
    public DriverReviewDTOMapper(ModelMapper modelMapper) {
        DriverReviewDTOMapper.modelMapper = modelMapper;
        modelMapper.typeMap(DriverReview.class, DriverReviewDTO.class).addMappings(mapper -> {
            mapper.map(src -> new PassengerIdEmailDTO(src.getReviewer().getId(), src.getReviewer().getEmail()),
                    DriverReviewDTO::setReviewer);
        });
    }

    public static DriverReviewDTO fromDriverReviewToDTO(DriverReview dto) {
        return modelMapper.map(dto, DriverReviewDTO.class);
    }
}
