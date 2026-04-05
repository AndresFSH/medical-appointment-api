package edu.unimag.medicalappointment.mapper;

import edu.unimag.medicalappointment.domain.entity.Appointment;
import edu.unimag.medicalappointment.dto.AppointmentDTO.*;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PatientMapper.class, DoctorMapper.class,
                                    OfficeMapper.class, AppointmentTypeMapper.class})
public interface AppointmentMapper {

    @Mapping(source = "id", target = "appointmentId")
    AppointmentResponse toResponse(Appointment appointment);

}

