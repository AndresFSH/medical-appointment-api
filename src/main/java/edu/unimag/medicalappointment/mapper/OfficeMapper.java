package edu.unimag.medicalappointment.mapper;

import edu.unimag.medicalappointment.domain.entity.Office;
import edu.unimag.medicalappointment.dto.OfficeDTO.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OfficeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "appointments", ignore = true)
    Office toEntity(CreateOfficeRequest req);

    @Mapping(target = "officeId", source = "id")
    OfficeResponse toResponse(Office office);

    @Mapping(target = "officeId", source = "id")
    OfficeSummary toSummary(Office office);

}
