package edu.unimag.medicalappointment.dto;

import edu.unimag.medicalappointment.domain.entity.enums.OfficeStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class OfficeDTO {

    public record CreateOfficeRequest(@NotBlank String name, @NotBlank String location){}
    public record UpdateOfficeRequest(@NotNull OfficeStatus status){}
    public record OfficeResponse(UUID officeId, String name, String location, OfficeStatus status){}
    public record OfficeSummary(UUID officeId, String name, String location){}

}
