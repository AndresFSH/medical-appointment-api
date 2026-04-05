package edu.unimag.medicalappointment.dto;

import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

import edu.unimag.medicalappointment.dto.DoctorDTO.DoctorSummary;

public class DoctorScheduleDTO {

    public record CreateDoctorScheduleRequest(@NotNull DayOfWeek dayOfWeek,@NotNull LocalTime startTime,
                                              @NotNull LocalTime endTime) {}
    public record DoctorScheduleResponse(UUID scheduleId, DoctorSummary doctor, DayOfWeek dayOfWeek,
                                         LocalTime startTime, LocalTime endTime) {}

}
