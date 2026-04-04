package edu.unimag.medicalappointment.repository.projection;

import java.util.UUID;

public record DoctorAppointmentCountProjection(UUID doctorId, Long appointmentCount) {
}
