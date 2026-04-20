package edu.unimag.medicalappointment.services;

import edu.unimag.medicalappointment.domain.entity.*;
import edu.unimag.medicalappointment.domain.entity.enums.AppointmentStatus;
import edu.unimag.medicalappointment.domain.entity.enums.OfficeStatus;
import edu.unimag.medicalappointment.domain.entity.enums.PatientStatus;
import edu.unimag.medicalappointment.dto.AppointmentDTO.*;
import edu.unimag.medicalappointment.dto.DoctorScheduleDTO.*;
import edu.unimag.medicalappointment.exception.BusinessException;
import edu.unimag.medicalappointment.exception.ConflictException;
import edu.unimag.medicalappointment.mapper.AppointmentMapper;
import edu.unimag.medicalappointment.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceTest {

    @Mock private AppointmentRepository appointmentRepo;
    @Mock private PatientRepository patientRepo;
    @Mock private DoctorRepository doctorRepo;
    @Mock private OfficeRepository officeRepo;
    @Mock private AppointmentTypeRepository appointmentTypeRepo;
    @Mock private DoctorScheduleService doctorScheduleService;
    @Mock private AppointmentMapper appointmentMapper;

    @InjectMocks private AppointmentServiceImpl appointmentService;


    private final List<AppointmentStatus> ACTIVE_STATUSES =
            List.of(AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED);

    private static final LocalDateTime BASE = LocalDateTime.of(2026, 6, 15, 10, 0);

    private UUID patientId;
    private UUID doctorId;
    private UUID officeId;
    private UUID typeId;
    private UUID appointmentId;

    private Patient activePatient;
    private Doctor activeDoctor;
    private Office availableOffice;
    private AppointmentType appointmentType;
    private DoctorScheduleResponse schedule;

    private CreateAppointmentRequest validRequest;

    @BeforeEach
    void setUp() {

        patientId = UUID.randomUUID();
        doctorId = UUID.randomUUID();
        officeId = UUID.randomUUID();
        typeId = UUID.randomUUID();
        appointmentId = UUID.randomUUID();

        activePatient = Patient.builder().fullName("Juan Pérez")
                .email("juan@mail.com").status(PatientStatus.ACTIVE).build();

        Specialty specialty = Specialty.builder().name("General medicine").build();

        activeDoctor = Doctor.builder().fullName("Dr. García").active(true).specialty(specialty).build();

        availableOffice = Office.builder().name("office 1").location("floor 1").status(OfficeStatus.AVAILABLE)
                .build();

        appointmentType = AppointmentType.builder().name("control").durationMinutes(60).build();

        schedule = new DoctorScheduleResponse(UUID.randomUUID(), null, DayOfWeek.MONDAY,
                LocalTime.of(8, 0), LocalTime.of(16, 0));

        validRequest = new CreateAppointmentRequest(patientId, doctorId, officeId, typeId, BASE);
    }

    @Test
    void create_whenStartAtInPast_throwsBusinessException() {
        CreateAppointmentRequest pastRequest = new CreateAppointmentRequest(patientId, doctorId, officeId,
                typeId, LocalDateTime.now().minusDays(1));
        setupValidMocks();
        assertThatThrownBy(() -> appointmentService.createAppointment(pastRequest)).
                isInstanceOf(BusinessException.class).hasMessageContaining("future");
        verify(appointmentRepo, never()).save(any());
    }

    @Test
    void create_WhenOutsideWorkingHours_throwsBusinessException() {
        CreateAppointmentRequest lateRequest = new CreateAppointmentRequest(patientId, doctorId, officeId,
                typeId, BASE.withHour(18));
        setupValidMocks();
        when(doctorScheduleService.findByDoctorIdAndDayOfWeek(any(), any())).thenReturn(schedule);
        assertThatThrownBy(() -> appointmentService.createAppointment(lateRequest)).
                isInstanceOf(BusinessException.class).hasMessageContaining("outside");
        verify(appointmentRepo, never()).save(any());
    }

    @Test
    void create_WhenExistsOverlappingDoctor_throwsConflictException() {
        setupValidMocksUntilSchedule();
        when(appointmentRepo.existsOverlappingDoctor(eq(doctorId), any(), any(), eq(ACTIVE_STATUSES)))
                .thenReturn(true);
        assertThatThrownBy(() -> appointmentService.createAppointment(validRequest))
                .isInstanceOf(ConflictException.class).hasMessageContaining("Doctor already has");
        verify(appointmentRepo, never()).save(any());
    }

    @Test
    void create_WhenExistsOverlappingOffice_throwsConflictException() {
        setupValidMocksUntilSchedule();
        when(appointmentRepo.existsOverlappingOffice(eq(officeId), any(), any(), eq(ACTIVE_STATUSES)))
                .thenReturn(true);
        assertThatThrownBy(() -> appointmentService.createAppointment(validRequest))
                .isInstanceOf(ConflictException.class).hasMessageContaining("Office already has");
    }


    @Test
    void create_WhenExistsOverlappingPatient_throwsConflictException() {
        setupValidMocksUntilSchedule();
        when(appointmentRepo.existsOverlappingPatient(eq(patientId), any(), any(), eq(ACTIVE_STATUSES)))
                .thenReturn(true);
        assertThatThrownBy(() -> appointmentService.createAppointment(validRequest))
                .isInstanceOf(ConflictException.class).hasMessageContaining("Patient already has");
    }


    @Test
    void create_calculatesEndAtFromDuration() {
        setupValidMocksUntilSchedule();
        setupNoOverlaps();

        Appointment appointment = Appointment.schedule(activePatient, activeDoctor, availableOffice,
                appointmentType, BASE);
        AppointmentResponse response = mock(AppointmentResponse.class);

        when(appointmentRepo.save(any())).thenReturn(appointment);
        when(appointmentMapper.toResponse(any())).thenReturn(response);

        appointmentService.createAppointment(validRequest);

        verify(appointmentRepo).save(argThat(a -> a.getEndAt().equals(BASE.plusMinutes(60))));
    }

    @Test
    void create_appointmentCreatedWithScheduledStatus_Successfully() {
        setupValidMocksUntilSchedule();
        setupNoOverlaps();

        Appointment savedAppointment = Appointment.schedule(activePatient, activeDoctor, availableOffice,
                appointmentType, BASE);
        when(appointmentRepo.save(any())).thenReturn(savedAppointment);
        when(appointmentMapper.toResponse(any())).thenReturn(mock(AppointmentResponse.class));

        ArgumentCaptor<Appointment> appointmentCaptor = ArgumentCaptor.forClass(Appointment.class);

        appointmentService.createAppointment(validRequest);

        verify(appointmentRepo, times(1)).save(appointmentCaptor.capture());

        Appointment saved = appointmentCaptor.getValue();

        assertThat(saved.getPatient()).isEqualTo(activePatient);
        assertThat(saved.getDoctor()).isEqualTo(activeDoctor);
        assertThat(saved.getOffice()).isEqualTo(availableOffice);
        assertThat(saved.getAppointmentType()).isEqualTo(appointmentType);

        assertThat(saved.getStartAt()).isEqualTo(BASE);
        assertThat(saved.getEndAt()).isEqualTo(BASE.plusMinutes(appointmentType.getDurationMinutes()));
        assertThat(saved.getStatus()).isEqualTo(AppointmentStatus.SCHEDULED);
    }

    @Test
    void cancel_WhenScheduled_CancelWithReason() {
        Appointment appointment = Appointment.schedule(activePatient, activeDoctor, availableOffice,
                appointmentType, BASE);
        CancelAppointmentRequest cancelRequest = new CancelAppointmentRequest("reason");

        when(appointmentRepo.findById(any())).thenReturn(Optional.of(appointment));
        when(appointmentMapper.toResponse(any())).thenReturn(mock(AppointmentResponse.class));

        appointmentService.cancelAppointment(appointmentId, cancelRequest);

        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.CANCELLED);
        assertThat(appointment.getCancellationReason()).isEqualTo("reason");
    }

    @Test
    void cancel_WhenConfirmed_CancelsSuccessfully() {
        Appointment appointment = Appointment.schedule(activePatient, activeDoctor, availableOffice,
                appointmentType, BASE);
        appointment.confirm();
        CancelAppointmentRequest req = new CancelAppointmentRequest("reason");

        when(appointmentRepo.findById(any())).thenReturn(Optional.of(appointment));
        when(appointmentMapper.toResponse(any())).thenReturn(mock(AppointmentResponse.class));

        appointmentService.cancelAppointment(appointmentId, req);

        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.CANCELLED);
        assertThat(appointment.getCancellationReason()).isEqualTo("reason");
    }

    @Test
    void cancel_WhenCompleted_ThrowsException() {
        Appointment appointment = Appointment.schedule(activePatient, activeDoctor, availableOffice,
                appointmentType, LocalDateTime.now().minusDays(1));
        appointment.confirm();
        appointment.complete("observations");
        CancelAppointmentRequest req = new CancelAppointmentRequest("reason");

        when(appointmentRepo.findById(any())).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> appointmentService.cancelAppointment(appointmentId, req))
                .isInstanceOf(IllegalStateException.class);
        verify(appointmentRepo, never()).save(any());
    }

    @Test
    void cancel_WhenNoShow_ThrowsException() {
        Appointment appointment = Appointment.schedule(activePatient, activeDoctor, availableOffice,
                appointmentType, LocalDateTime.now().minusDays(1));
        appointment.confirm();
        appointment.markNoShow();
        CancelAppointmentRequest req = new CancelAppointmentRequest("reason");

        when(appointmentRepo.findById(any())).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> appointmentService.cancelAppointment(appointmentId, req))
                .isInstanceOf(IllegalStateException.class);
        verify(appointmentRepo, never()).save(any());
    }

    @Test
    void complete_WhenConfirmedAndAfterStartTime_Successfully() {
        Appointment appointment = Appointment.schedule(activePatient, activeDoctor, availableOffice,
                appointmentType, LocalDateTime.now().minusHours(2));
        appointment.confirm();
        CompleteAppointmentRequest req = new CompleteAppointmentRequest("observations");

        when(appointmentRepo.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(appointmentRepo.save(any())).thenReturn(appointment);
        when(appointmentMapper.toResponse(any())).thenReturn(mock(AppointmentResponse.class));

        appointmentService.completeAppointment(appointmentId, req);

        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.COMPLETED);
        assertThat(appointment.getObservations()).isEqualTo("observations");
    }

    @Test
    void complete_beforeStartTime_throwsBusinessException() {
        Appointment appointment = Appointment.schedule(activePatient, activeDoctor, availableOffice,
                appointmentType, BASE);
        appointment.confirm();
        CompleteAppointmentRequest req = new CompleteAppointmentRequest("observations");

        when(appointmentRepo.findById(appointmentId)).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> appointmentService.completeAppointment(appointmentId, req))
                .isInstanceOf(BusinessException.class).hasMessageContaining("before");
        verify(appointmentRepo, never()).save(any());
    }

    @Test
    void complete_WhenNotConfirmed_throwsException() {
        Appointment appointment = Appointment.schedule(activePatient, activeDoctor, availableOffice,
                appointmentType, LocalDateTime.now().minusHours(2));
        CompleteAppointmentRequest req = new CompleteAppointmentRequest("observations");

        when(appointmentRepo.findById(appointmentId)).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> appointmentService.completeAppointment(appointmentId, req))
                .isInstanceOf(IllegalStateException.class);
        verify(appointmentRepo, never()).save(any());
    }

    @Test
    void noShow_WhenConfirmedAndAfterStartTime_Successfully() {
        Appointment appointment = Appointment.schedule(activePatient, activeDoctor, availableOffice,
                appointmentType, LocalDateTime.now().minusHours(2));
        appointment.confirm();

        when(appointmentRepo.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(appointmentRepo.save(any())).thenReturn(appointment);
        when(appointmentMapper.toResponse(any())).thenReturn(mock(AppointmentResponse.class));

        appointmentService.setAsNoShowAppointment(appointmentId);

        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.NO_SHOW);
    }

    @Test
    void noShow_WhenBeforeStartTime_throwsBusinessException() {
        Appointment appointment = Appointment.schedule(activePatient, activeDoctor, availableOffice,
                appointmentType, BASE);
        appointment.confirm();

        when(appointmentRepo.findById(appointmentId)).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> appointmentService.setAsNoShowAppointment(appointmentId))
                .isInstanceOf(BusinessException.class).hasMessageContaining("before");
        verify(appointmentRepo, never()).save(any());
    }

    @Test
    void noShow_WhenNotConfirmed_throwsException() {
        Appointment appointment = Appointment.schedule(activePatient, activeDoctor, availableOffice,
                appointmentType, LocalDateTime.now().minusHours(2));

        when(appointmentRepo.findById(appointmentId)).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> appointmentService.setAsNoShowAppointment(appointmentId))
                .isInstanceOf(IllegalStateException.class);
        verify(appointmentRepo, never()).save(any());
    }

    private void setupValidMocks() {
        when(patientRepo.findById(patientId)).thenReturn(Optional.of(activePatient));
        when(doctorRepo.findById(doctorId)).thenReturn(Optional.of(activeDoctor));
        when(officeRepo.findById(officeId)).thenReturn(Optional.of(availableOffice));
        when(appointmentTypeRepo.findById(typeId)).thenReturn(Optional.of(appointmentType));
    }

    private void setupValidMocksUntilSchedule() {
        when(patientRepo.findById(patientId)).thenReturn(Optional.of(activePatient));
        when(doctorRepo.findById(doctorId)).thenReturn(Optional.of(activeDoctor));
        when(officeRepo.findById(officeId)).thenReturn(Optional.of(availableOffice));
        when(appointmentTypeRepo.findById(typeId)).thenReturn(Optional.of(appointmentType));
        when(doctorScheduleService.findByDoctorIdAndDayOfWeek(any(), any())).thenReturn(schedule);
    }

    private void setupNoOverlaps() {
        when(appointmentRepo.existsOverlappingDoctor(any(), any(), any(), any())).thenReturn(false);
        when(appointmentRepo.existsOverlappingOffice(any(), any(), any(), any())).thenReturn(false);
        when(appointmentRepo.existsOverlappingPatient(any(), any(), any(), any())).thenReturn(false);
    }

}
