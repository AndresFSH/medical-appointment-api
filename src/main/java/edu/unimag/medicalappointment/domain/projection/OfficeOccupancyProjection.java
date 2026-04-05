package edu.unimag.medicalappointment.domain.projection;

import java.util.UUID;

public record OfficeOccupancyProjection(UUID officeId, String officeName, Long appointmentCount) {
}
