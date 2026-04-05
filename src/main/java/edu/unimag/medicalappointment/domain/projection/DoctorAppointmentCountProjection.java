package edu.unimag.medicalappointment.domain.projection;

import java.util.UUID;

public record DoctorAppointmentCountProjection(UUID doctorId, String doctorName,Long appointmentCount) {
}
