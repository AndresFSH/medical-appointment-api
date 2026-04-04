package edu.unimag.medicalappointment.repository.projection;

import java.util.UUID;

public record PatientNoShowAppointmentProjection(UUID patientId, String patientName, Long appointmentCount) {
}
