package edu.unimag.medicalappointment.services;

import edu.unimag.medicalappointment.domain.entity.*;
import edu.unimag.medicalappointment.domain.entity.enums.AppointmentStatus;
import edu.unimag.medicalappointment.domain.entity.enums.OfficeStatus;
import edu.unimag.medicalappointment.domain.entity.enums.PatientStatus;
import edu.unimag.medicalappointment.dto.AvailabilityDTO.*;
import edu.unimag.medicalappointment.dto.DoctorScheduleDTO.*;
import edu.unimag.medicalappointment.exception.ResourceNotFoundException;
import edu.unimag.medicalappointment.repository.AppointmentRepository;
import edu.unimag.medicalappointment.repository.AppointmentTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvailabilityServiceImplTest {

    @Mock private DoctorScheduleService doctorScheduleService;
    @Mock private AppointmentTypeRepository appointmentTypeRepo;
    @Mock private AppointmentRepository appointmentRepo;

    @InjectMocks private AvailabilityServiceImpl availabilityService;

    private static final List<AppointmentStatus> ACTIVE_STATUSES =
            List.of(AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED);

    private static final LocalDate DATE = LocalDate.of(2026, 6, 15);

    private UUID doctorId;
    private UUID appointmentTypeId;

    private DoctorScheduleResponse schedule;
    private AppointmentType typeOf60min;

    private Doctor doctor;
    private Patient patient;
    private Office office;

    @BeforeEach
    void setUp() {
        doctorId = UUID.randomUUID();
        appointmentTypeId = UUID.randomUUID();

        schedule = new DoctorScheduleResponse(UUID.randomUUID(), null, DayOfWeek.MONDAY,
                LocalTime.of(8, 0), LocalTime.of(10, 0));

        typeOf60min = AppointmentType.builder().name("general").durationMinutes(60).build();

        Specialty specialty = Specialty.builder().name("specialty").build();
        doctor = Doctor.builder().fullName("doctor").active(true).specialty(specialty).build();
        patient = Patient.builder().fullName("patient").email("patient@mail.com").status(PatientStatus.ACTIVE).build();
        office = Office.builder().name("office1").location("location1").status(OfficeStatus.AVAILABLE).build();
    }

    @Test
    void getAvailabilitySlot_WhenNoBooked_ReturnsAllSlots() {
        when(doctorScheduleService.findByDoctorIdAndDayOfWeek(doctorId, DayOfWeek.MONDAY)).thenReturn(schedule);
        when(appointmentRepo.findByDoctorIdAndDate(eq(doctorId), eq(DATE), eq(ACTIVE_STATUSES))).thenReturn(List.of());
        when(appointmentTypeRepo.findById(appointmentTypeId)).thenReturn(Optional.of(typeOf60min));

        List<AvailabilitySlotResponse> result =
                availabilityService.getAvailabilitySlot(doctorId, DATE, appointmentTypeId);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).startAt()).isEqualTo(LocalDateTime.of(DATE, LocalTime.of(8, 0)));
        assertThat(result.get(0).endAt()).isEqualTo(LocalDateTime.of(DATE, LocalTime.of(9, 0)));
        assertThat(result.get(1).startAt()).isEqualTo(LocalDateTime.of(DATE, LocalTime.of(9, 0)));
        assertThat(result.get(1).endAt()).isEqualTo(LocalDateTime.of(DATE, LocalTime.of(10, 0)));
    }

    @Test
    void getSlots_whenOneAppointmentBooked_excludesThatSlot() {
        Appointment bookedAppointment = Appointment.schedule(patient, doctor, office, typeOf60min,
                LocalDateTime.of(DATE, LocalTime.of(8, 0))
        );

        when(doctorScheduleService.findByDoctorIdAndDayOfWeek(doctorId, DayOfWeek.MONDAY)).thenReturn(schedule);
        when(appointmentRepo.findByDoctorIdAndDate(eq(doctorId), eq(DATE), eq(ACTIVE_STATUSES)))
                .thenReturn(List.of(bookedAppointment));
        when(appointmentTypeRepo.findById(appointmentTypeId)).thenReturn(Optional.of(typeOf60min));

        List<AvailabilitySlotResponse> result =
                availabilityService.getAvailabilitySlot(doctorId, DATE, appointmentTypeId);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().startAt()).isEqualTo(LocalDateTime.of(DATE, LocalTime.of(9, 0)));
    }

    @Test
    void getSlots_whenAllBooked_returnsEmpty() {
        Appointment first = Appointment.schedule(patient, doctor, office, typeOf60min,
                LocalDateTime.of(DATE, LocalTime.of(8, 0))
        );
        Appointment second = Appointment.schedule(patient, doctor, office, typeOf60min,
                LocalDateTime.of(DATE, LocalTime.of(9, 0))
        );

        when(doctorScheduleService.findByDoctorIdAndDayOfWeek(doctorId, DayOfWeek.MONDAY)).thenReturn(schedule);
        when(appointmentRepo.findByDoctorIdAndDate(eq(doctorId), eq(DATE), eq(ACTIVE_STATUSES)))
                .thenReturn(List.of(first, second));
        when(appointmentTypeRepo.findById(appointmentTypeId)).thenReturn(Optional.of(typeOf60min));

        List<AvailabilitySlotResponse> result = availabilityService
                .getAvailabilitySlot(doctorId, DATE, appointmentTypeId);

        assertThat(result).isEmpty();
    }

    @Test
    void getSlots_whenAppointmentTypeNotFound_throwsResourceNotFoundException() {
        when(doctorScheduleService.findByDoctorIdAndDayOfWeek(doctorId, DayOfWeek.MONDAY)).thenReturn(schedule);
        when(appointmentRepo.findByDoctorIdAndDate(any(), any(), any())).thenReturn(List.of());
        when(appointmentTypeRepo.findById(appointmentTypeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> availabilityService.getAvailabilitySlot(doctorId, DATE, appointmentTypeId))
                .isInstanceOf(ResourceNotFoundException.class).hasMessageContaining(appointmentTypeId.toString());
    }

    @Test
    void getSlots_whenNoScheduleForDay_throwsResourceNotFoundException() {
        when(doctorScheduleService.findByDoctorIdAndDayOfWeek(doctorId, DayOfWeek.MONDAY))
                .thenThrow(new ResourceNotFoundException("Schedule not found"));

        assertThatThrownBy(() ->availabilityService.getAvailabilitySlot(doctorId, DATE, appointmentTypeId))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}

