package edu.unimag.medicalappointment.domain.projection;

import java.util.UUID;

public record PatientNoShowAppointmentProjection(UUID patientId, String patientName, Long appointmentCount) {
}
