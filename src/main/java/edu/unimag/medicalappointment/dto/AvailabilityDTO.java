package edu.unimag.medicalappointment.dto;

import java.time.LocalDateTime;

public class AvailabilityDTO {

    public record AvailabilitySlotResponse(LocalDateTime startAt, LocalDateTime endAt) {}

}
