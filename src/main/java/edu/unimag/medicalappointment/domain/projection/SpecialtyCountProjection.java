package edu.unimag.medicalappointment.domain.projection;

import java.util.UUID;

public record SpecialtyCountProjection(UUID specialtyId, String specialtyName,Long appointmentCount) {
}
