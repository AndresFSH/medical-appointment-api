package edu.unimag.medicalappointment.repository.projection;

import java.util.UUID;

public record OfficeOccupancyProjection(UUID officeId, String officeName, Long appointmentCount) {
}
