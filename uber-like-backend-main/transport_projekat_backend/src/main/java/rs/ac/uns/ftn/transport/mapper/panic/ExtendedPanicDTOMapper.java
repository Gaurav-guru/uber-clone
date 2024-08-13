package rs.ac.uns.ftn.transport.mapper.panic;

import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.transport.dto.panic.ExtendedPanicDTO;
import rs.ac.uns.ftn.transport.model.Panic;
import rs.ac.uns.ftn.transport.service.interfaces.IVehicleTypeService;

@Component
public class ExtendedPanicDTOMapper {

    private static ModelMapper modelMapper;
    private static IVehicleTypeService vehicleTypeService;
    @Autowired
    public ExtendedPanicDTOMapper(ModelMapper modelMapper, IVehicleTypeService vehicleTypeService){

        ExtendedPanicDTOMapper.modelMapper = modelMapper;
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE)
                .setMatchingStrategy(MatchingStrategies.STANDARD);

        modelMapper.typeMap(Panic.class, ExtendedPanicDTO.class).addMappings(mapper -> {
            mapper.map(src -> src.getUser(),
                    ExtendedPanicDTO::setUser);
        });
        ExtendedPanicDTOMapper.vehicleTypeService = vehicleTypeService;

        modelMapper.typeMap(ExtendedPanicDTO.class, Panic.class).addMappings(mapper -> {
            mapper.map(src -> src.getUser(),
                    Panic::setUser);
        });


    }

    public static Panic fromDTOtoPanic(ExtendedPanicDTO dto) {

        return modelMapper.map(dto,  Panic.class);
    }

    public static ExtendedPanicDTO fromPanicToDTO(Panic model) {
        String vehicleTypeName = model.getRide().getVehicleType().getName();
        ExtendedPanicDTO dto = modelMapper.map(model,ExtendedPanicDTO.class);
        dto.getRide().setVehicleType(vehicleTypeName);
        return dto;
    }
}
