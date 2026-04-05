package edu.unimag.medicalappointment.mapper;

import edu.unimag.medicalappointment.domain.entity.DoctorSchedule;
import edu.unimag.medicalappointment.dto.DoctorScheduleDTO.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DoctorScheduleMapper {

    @Mapping(target = "scheduleId", source = "id")
    DoctorScheduleResponse toResponse(DoctorSchedule doctorSchedule);

}
