package edu.unimag.medicalappointment.repository.projection;

import java.util.UUID;

public record SpecialtyCountProjection(UUID specialtyId, String specialtyName,Long appointmentCount) {
}
