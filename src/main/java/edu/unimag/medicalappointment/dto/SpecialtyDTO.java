package edu.unimag.medicalappointment.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public class SpecialtyDTO {

    public record CreateSpecialtyRequest(@NotBlank String name) {}
    public record SpecialtyResponse(UUID specialtyId, String name) {}
    public record SpecialtySummary(UUID specialtyId, String name) {}
}
