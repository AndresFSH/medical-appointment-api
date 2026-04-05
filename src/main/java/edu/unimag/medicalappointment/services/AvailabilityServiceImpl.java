package edu.unimag.medicalappointment.services;

import edu.unimag.medicalappointment.domain.entity.AppointmentType;
import edu.unimag.medicalappointment.domain.entity.enums.AppointmentStatus;
import edu.unimag.medicalappointment.dto.AvailabilityDTO.*;
import edu.unimag.medicalappointment.dto.DoctorScheduleDTO.*;
import edu.unimag.medicalappointment.exception.ResourceNotFoundException;
import edu.unimag.medicalappointment.repository.AppointmentRepository;
import edu.unimag.medicalappointment.repository.AppointmentTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AvailabilityServiceImpl implements AvailabilityService {

    private final DoctorScheduleService doctorScheduleService;
    private final AppointmentTypeRepository appointmentTypeRepo;
    private final AppointmentRepository appointmentRepo;

    private static final List<AppointmentStatus> ACTIVE_STATUSES =
            List.of(AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED);

    @Override
    @Transactional(readOnly = true)
    public List<AvailabilitySlotResponse> getAvailabilitySlot(UUID doctorId, LocalDate date, UUID appointmentTypeId) {
        DoctorScheduleResponse schedule = doctorScheduleService.
                findByDoctorIdAndDayOfWeek(doctorId, date.getDayOfWeek());

        var bookedAppointments = appointmentRepo.findByDoctorIdAndDate(doctorId, date, ACTIVE_STATUSES);
        AppointmentType appointmentType = appointmentTypeRepo.findById(appointmentTypeId).
                orElseThrow(()-> new ResourceNotFoundException("Appointment type with id "+
                                                                appointmentTypeId+" not found"));

        int slotDurationMinutes = appointmentType.getDurationMinutes();

        List<AvailabilitySlotResponse> availabilitySlots = new ArrayList<>();
        LocalTime current = schedule.startTime();
        LocalTime workEndTime = schedule.endTime();

        while(!current.plusMinutes(slotDurationMinutes).isAfter(workEndTime)) {
            LocalDateTime slotStart = LocalDateTime.of(date,current);
            LocalDateTime slotEnd = slotStart.plusMinutes(slotDurationMinutes);

            boolean isBooked = bookedAppointments.stream().anyMatch(appointment ->
                    appointment.getStartAt().isBefore(slotEnd) && appointment.getEndAt().isAfter(slotStart)
            );

            if(!isBooked) {
                availabilitySlots.add(new AvailabilitySlotResponse(slotStart,slotEnd));
            }
            current = current.plusMinutes(slotDurationMinutes);
        }

        return availabilitySlots;
    }

}
