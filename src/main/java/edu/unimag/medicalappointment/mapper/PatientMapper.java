package edu.unimag.medicalappointment.mapper;

import edu.unimag.medicalappointment.domain.entity.Patient;
import edu.unimag.medicalappointment.dto.PatientDTO.*;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PatientMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "appointments", ignore = true)
    @Mapping(target = "status", ignore = true)
    Patient toEntity(CreatePatientRequest req);

    @Mapping(source = "id", target = "patientId")
    PatientResponse toResponse(Patient patient);

    @Mapping(source = "id", target = "patientId")
    PatientSummary toSummary(Patient patient);
}
