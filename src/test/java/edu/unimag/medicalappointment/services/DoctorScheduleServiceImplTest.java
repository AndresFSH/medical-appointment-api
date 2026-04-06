package edu.unimag.medicalappointment.services;

import edu.unimag.medicalappointment.domain.entity.Doctor;
import edu.unimag.medicalappointment.domain.entity.DoctorSchedule;
import edu.unimag.medicalappointment.domain.entity.Specialty;
import edu.unimag.medicalappointment.dto.DoctorScheduleDTO.*;
import edu.unimag.medicalappointment.exception.ConflictException;
import edu.unimag.medicalappointment.exception.ResourceNotFoundException;
import edu.unimag.medicalappointment.mapper.DoctorScheduleMapper;
import edu.unimag.medicalappointment.repository.DoctorRepository;
import edu.unimag.medicalappointment.repository.DoctorScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorScheduleServiceImplTest {

    @Mock private DoctorScheduleRepository doctorScheduleRepo;
    @Mock private DoctorRepository doctorRepo;
    @Mock private DoctorScheduleMapper doctorScheduleMapper;

    @InjectMocks private DoctorScheduleServiceImpl doctorScheduleService;

    private UUID doctorId;
    private Doctor doctor;
    private CreateDoctorScheduleRequest validRequest;
    private DoctorSchedule doctorSchedule;
    private DoctorScheduleResponse scheduleResponse;

    @BeforeEach
    void setUp() {
        doctorId = UUID.randomUUID();
        Specialty specialty = Specialty.builder().name("general").build();
        doctor = Doctor.builder().fullName("doctor").active(true).specialty(specialty).build();

        validRequest = new CreateDoctorScheduleRequest(DayOfWeek.MONDAY, LocalTime.of(8,0),
                LocalTime.of(16,0));

        doctorSchedule = DoctorSchedule.create(doctor, DayOfWeek.MONDAY, LocalTime.of(8,0),
                LocalTime.of(16,0));

        scheduleResponse = new DoctorScheduleResponse(UUID.randomUUID(), null, DayOfWeek.MONDAY,
                LocalTime.of(8,0), LocalTime.of(16,0));
    }

    @Test
    void createDoctorSchedule_WhenNoDuplicateDay_Successfully() {
        when(doctorRepo.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(doctorScheduleRepo.existsByDoctorIdAndDayOfWeek(doctorId, DayOfWeek.MONDAY)).thenReturn(false);
        when(doctorScheduleRepo.save(any())).thenReturn(doctorSchedule);
        when(doctorScheduleMapper.toResponse(any())).thenReturn(scheduleResponse);

        DoctorScheduleResponse result = doctorScheduleService.createDoctorSchedule(doctorId, validRequest);

        assertThat(result).isEqualTo(scheduleResponse);
        verify(doctorScheduleRepo).save(any(DoctorSchedule.class));
    }

    @Test
    void create_whenDoctorNotFound_throwsResourceNotFoundException() {
        when(doctorRepo.findById(doctorId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> doctorScheduleService.createDoctorSchedule(doctorId, validRequest))
                .isInstanceOf(ResourceNotFoundException.class).hasMessageContaining(doctorId.toString());
        verify(doctorScheduleRepo, never()).save(any());
    }

    @Test
    void create_whenScheduleAlreadyExists_throwsConflictException() {
        when(doctorRepo.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(doctorScheduleRepo.existsByDoctorIdAndDayOfWeek(doctorId, DayOfWeek.MONDAY)).thenReturn(true);

        assertThatThrownBy(() -> doctorScheduleService.createDoctorSchedule(doctorId, validRequest))
                .isInstanceOf(ConflictException.class).hasMessageContaining(DayOfWeek.MONDAY.toString());
        verify(doctorScheduleRepo, never()).save(any());
    }

    @Test
    void create_whenInvalidTimeRange_throwsException() {
        CreateDoctorScheduleRequest invalidRequest = new CreateDoctorScheduleRequest(DayOfWeek.MONDAY,
                LocalTime.of(16, 0), LocalTime.of(8, 0));

        when(doctorRepo.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(doctorScheduleRepo.existsByDoctorIdAndDayOfWeek(doctorId, DayOfWeek.MONDAY)).thenReturn(false);

        assertThatThrownBy(() -> doctorScheduleService.createDoctorSchedule(doctorId, invalidRequest))
                .isInstanceOf(IllegalArgumentException.class);
        verify(doctorScheduleRepo, never()).save(any());
    }

    @Test
    void findByDoctorIdAndDayOfWeek_whenExists_returnsDoctorSchedule() {
        when(doctorScheduleRepo.findByDoctorIdAndDayOfWeek(doctorId, DayOfWeek.MONDAY))
                .thenReturn(Optional.of(doctorSchedule));
        when(doctorScheduleMapper.toResponse(doctorSchedule)).thenReturn(scheduleResponse);

        DoctorScheduleResponse result = doctorScheduleService.findByDoctorIdAndDayOfWeek(doctorId, DayOfWeek.MONDAY);

        assertThat(result).isEqualTo(scheduleResponse);
    }

    @Test
    void find_whenNotExists_throwsResourceNotFoundException() {
        when(doctorScheduleRepo.findByDoctorIdAndDayOfWeek(doctorId, DayOfWeek.TUESDAY)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> doctorScheduleService.findByDoctorIdAndDayOfWeek(doctorId, DayOfWeek.TUESDAY))
                .isInstanceOf(ResourceNotFoundException.class).hasMessageContaining(DayOfWeek.TUESDAY.toString());
    }

    @Test
    void findAll_returnsAllDoctorSchedules() {
        when(doctorScheduleRepo.findByDoctorId(doctorId)).thenReturn(List.of(doctorSchedule));
        when(doctorScheduleMapper.toResponse(doctorSchedule)).thenReturn(scheduleResponse);

        List<DoctorScheduleResponse> result = doctorScheduleService.findAll(doctorId);

        assertThat(result).hasSize(1).contains(scheduleResponse);
    }

    @Test
    void findAll_whenNoSchedules_returnsEmptyList() {
        when(doctorScheduleRepo.findByDoctorId(doctorId)).thenReturn(List.of());
        List<DoctorScheduleResponse> result = doctorScheduleService.findAll(doctorId);
        assertThat(result).isEmpty();
    }

}