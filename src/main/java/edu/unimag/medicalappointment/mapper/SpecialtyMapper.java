package edu.unimag.medicalappointment.mapper;

import edu.unimag.medicalappointment.domain.entity.Specialty;
import org.mapstruct.Mapper;

import edu.unimag.medicalappointment.dto.SpecialtyDTO.*;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SpecialtyMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "doctors", ignore = true)
    @Mapping(source = "name", target = "name")
    Specialty toEntity(CreateSpecialtyRequest req);

    @Mapping(source = "id", target = "specialtyId")
    SpecialtyResponse toResponse(Specialty specialty);

    @Mapping(source = "id", target = "specialtyId")
    SpecialtySummary toSummary(Specialty specialty);

}
