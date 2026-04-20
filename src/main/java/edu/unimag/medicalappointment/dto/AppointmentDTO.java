package edu.unimag.medicalappointment.dto;

import edu.unimag.medicalappointment.domain.entity.enums.AppointmentStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

import edu.unimag.medicalappointment.dto.PatientDTO.PatientSummary;
import edu.unimag.medicalappointment.dto.DoctorDTO.DoctorSummary;
import edu.unimag.medicalappointment.dto.OfficeDTO.OfficeSummary;
import edu.unimag.medicalappointment.dto.AppointmentTypeDTO.AppointmentTypeSummary;

public class AppointmentDTO {

    public record CreateAppointmentRequest(@NotNull UUID patientId, @NotNull UUID doctorId,
                                           @NotNull UUID officeId, @NotNull UUID typeId,
                                           @NotNull @Future LocalDateTime startAt) {}

    public record CancelAppointmentRequest(@NotBlank String cancellationReason) {}

    public record CompleteAppointmentRequest(String observations){}

    public record AppointmentResponse(UUID appointmentId, PatientSummary patient, DoctorSummary doctor,
                                      OfficeSummary office, AppointmentTypeSummary appointmentType,
                                      LocalDateTime startAt, LocalDateTime endAt, AppointmentStatus status,
                                      String cancellationReason, String observations) {}


}
