package rs.ac.uns.ftn.transport.mapper.workinghours;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.transport.dto.workinghours.WorkingHoursDTO;
import rs.ac.uns.ftn.transport.model.WorkingHours;

@Component
public class WorkingHoursDTOMapper {
    private static ModelMapper modelMapper;

    @Autowired
    public WorkingHoursDTOMapper(ModelMapper modelMapper) {
        WorkingHoursDTOMapper.modelMapper = modelMapper;
    }

    public static WorkingHours fromDTOToWorkingHours(WorkingHoursDTO dto) {
        return modelMapper.map(dto, WorkingHours.class);
    }

    public static WorkingHoursDTO fromWorkingHoursToDTO(WorkingHours workingHours) {
        return modelMapper.map(workingHours, WorkingHoursDTO.class);
    }
}