package rs.ac.uns.ftn.transport.mapper.workinghours;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.transport.dto.workinghours.WorkingHoursDTO;
import rs.ac.uns.ftn.transport.dto.workinghours.WorkingHoursStartDTO;
import rs.ac.uns.ftn.transport.model.WorkingHours;

@Component
public class WorkingHoursStartDTOMapper {
    private static ModelMapper modelMapper;

    @Autowired
    public WorkingHoursStartDTOMapper(ModelMapper modelMapper) {
        WorkingHoursStartDTOMapper.modelMapper = modelMapper;
    }

    public static WorkingHours fromDTOToWorkingHoursStart(WorkingHoursStartDTO dto) {
        return modelMapper.map(dto, WorkingHours.class);
    }

    public static WorkingHoursStartDTO fromWorkingHoursToStartDTO(WorkingHours workingHours) {
        return modelMapper.map(workingHours, WorkingHoursStartDTO.class);
    }
}