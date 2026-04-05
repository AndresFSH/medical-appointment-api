package edu.unimag.medicalappointment.mapper;

import edu.unimag.medicalappointment.domain.entity.AppointmentType;
import org.mapstruct.Mapper;

import edu.unimag.medicalappointment.dto.AppointmentTypeDTO.*;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AppointmentTypeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "appointments", ignore = true)
    AppointmentType toEntity(CreateAppointmentTypeRequest req);

    AppointmentTypeResponse toResponse(AppointmentType appointmentType);

    @Mapping(target = "appointmentTypeId", source = "id")
    @Mapping(target = "appointmentTypeName", source = "name")
    AppointmentTypeSummary toSummary(AppointmentType appointmentType);

}
