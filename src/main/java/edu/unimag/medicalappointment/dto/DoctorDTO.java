package edu.unimag.medicalappointment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import edu.unimag.medicalappointment.dto.SpecialtyDTO.SpecialtySummary;

import java.util.UUID;

public class DoctorDTO {

    public record CreateDoctorRequest(@NotBlank String fullName, @NotNull UUID specialtyId){}
    public record UpdateDoctorRequest(@NotBlank String fullName, boolean active, @NotNull UUID specialtyId){}
    public record DoctorResponse(UUID doctorId, String fullName, boolean active, SpecialtySummary specialty){}
    public record DoctorSummary(UUID doctorId, String fullName, SpecialtySummary specialty){}

}
