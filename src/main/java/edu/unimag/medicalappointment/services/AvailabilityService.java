package edu.unimag.medicalappointment.services;

import edu.unimag.medicalappointment.domain.entity.AppointmentType;
import edu.unimag.medicalappointment.dto.AvailabilityDTO.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AvailabilityService {

    List<AvailabilitySlotResponse> getAvailabilitySlot(UUID doctorId, LocalDate date, UUID appointmentTypeId);

}
