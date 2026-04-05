package edu.unimag.medicalappointment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class AppointmentTypeDTO {

    public record CreateAppointmentTypeRequest(@NotBlank String name, @NotNull Integer durationMinutes) {}
    public record AppointmentTypeResponse(UUID id, String name, Integer durationMinutes) {}
    public record AppointmentTypeSummary(UUID appointmentTypeId, String appointmentTypeName) {}

}
