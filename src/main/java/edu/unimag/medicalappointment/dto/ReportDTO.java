package edu.unimag.medicalappointment.dto;

import java.util.UUID;

public class ReportDTO {

    public record OfficeOccupancyResponse(UUID officeId, String officeName, Long appointmentCount) {}
    public record DoctorProductivityResponse(UUID doctorId, String doctorName, Long completedAppointmentCount) {}
    public record NoShowPatientResponse(UUID patientId, String patientName, Long noShowAppointmentCount) {}

}
