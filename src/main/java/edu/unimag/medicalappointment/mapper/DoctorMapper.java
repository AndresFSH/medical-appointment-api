package edu.unimag.medicalappointment.mapper;

import edu.unimag.medicalappointment.domain.entity.Doctor;
import edu.unimag.medicalappointment.dto.DoctorDTO.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DoctorMapper {

    @Mapping(source = "id",target = "doctorId")
    DoctorResponse toResponse(Doctor doctor);

    @Mapping(source = "id", target = "doctorId")
    DoctorSummary toSummary(Doctor doctor);


}
