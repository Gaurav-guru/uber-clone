package rs.ac.uns.ftn.transport.mapper.workinghours;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.transport.dto.workinghours.WorkingHoursDTO;
import rs.ac.uns.ftn.transport.dto.workinghours.WorkingHoursEndDTO;
import rs.ac.uns.ftn.transport.model.WorkingHours;

@Component
public class WorkingHoursEndDTOMapper {
    private static ModelMapper modelMapper;

    @Autowired
    public WorkingHoursEndDTOMapper(ModelMapper modelMapper) {
        WorkingHoursEndDTOMapper.modelMapper = modelMapper;
    }

    public static WorkingHours fromDTOToWorkingHoursEnd(WorkingHoursEndDTO dto) {
        return modelMapper.map(dto, WorkingHours.class);
    }

    public static WorkingHoursEndDTO fromWorkingHoursToEndDTO(WorkingHours workingHours) {
        return modelMapper.map(workingHours, WorkingHoursEndDTO.class);
    }
}