package edu.unimag.medicalappointment.dto;

import edu.unimag.medicalappointment.domain.entity.enums.PatientStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class PatientDTO {
    public record CreatePatientRequest(@NotBlank String fullName, @NotBlank @Email String email) {}
    public record UpdatePatientRequest(@NotBlank String fullName, @Email String email, @NotNull PatientStatus status) {}
    public record PatientResponse(UUID patientId, String fullName, String email, PatientStatus status) {}
    public record PatientSummary(UUID patientId, String fullName){}
}
