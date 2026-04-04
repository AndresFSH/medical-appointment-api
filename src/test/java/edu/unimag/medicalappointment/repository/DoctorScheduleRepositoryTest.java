package edu.unimag.medicalappointment.repository;

import edu.unimag.medicalappointment.domain.entity.Doctor;
import edu.unimag.medicalappointment.domain.entity.DoctorSchedule;
import edu.unimag.medicalappointment.domain.entity.Specialty;
import edu.unimag.medicalappointment.testutil.DoctorRepositoryTestFactory;
import edu.unimag.medicalappointment.testutil.DoctorScheduleRepositoryTestFactory;
import edu.unimag.medicalappointment.testutil.SpecialtyRepositoryTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class DoctorScheduleRepositoryTest extends AbstractRepositoryIT {

    @Autowired DoctorScheduleRepository doctorScheduleRepo;
    @Autowired DoctorRepository doctorRepo;
    @Autowired SpecialtyRepository specialtyRepo;

    private Doctor doctor;
    private static final LocalTime START_TIME = LocalTime.of(8,0);
    private static final LocalTime END_TIME = LocalTime.of(16,0);

    @BeforeEach
    void setUp() {
        Specialty specialty = specialtyRepo.save(SpecialtyRepositoryTestFactory.create());
        doctor = doctorRepo.save(DoctorRepositoryTestFactory.create(specialty));
    }

    @Test
    @DisplayName("Returns a doctor schedule with a given id")
    void shouldCreateAndFindById(){
        var saved = doctorScheduleRepo.save(DoctorScheduleRepositoryTestFactory.create(doctor,
                DayOfWeek.FRIDAY, START_TIME, END_TIME));
        var result = doctorScheduleRepo.findById(saved.getId());
        assertThat(result).isPresent().get().extracting(DoctorSchedule::getId).isEqualTo(saved.getId());
    }

    @Test
    @DisplayName("Returns a doctor's schedule by day of the week")
    void shouldFindByDoctorIdAndDayOfWeek() {
        doctorScheduleRepo.save(DoctorScheduleRepositoryTestFactory.create(doctor,
                DayOfWeek.FRIDAY, START_TIME, END_TIME));

        var result = doctorScheduleRepo.findByDoctorIdAndDayOfWeek(doctor.getId(), DayOfWeek.FRIDAY);

        assertThat(result).isPresent().get().extracting(ds -> ds.getDoctor().getId()).isEqualTo(doctor.getId());
        assertThat(result).get().extracting(DoctorSchedule::getDayOfWeek).isEqualTo(DayOfWeek.FRIDAY);
    }

    @Test
    @DisplayName("Returns true if a doctor's schedule exists for a given doctor ID and day of the week")
    void existsByDoctorIdAndDayOfWeek_whenExists_returnsTrue() {
        doctorScheduleRepo.save(DoctorScheduleRepositoryTestFactory.create(doctor,
                DayOfWeek.FRIDAY, START_TIME, END_TIME));

        var result = doctorScheduleRepo.existsByDoctorIdAndDayOfWeek(doctor.getId(), DayOfWeek.FRIDAY);
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Returns false if a doctor's schedule does not exist for a given doctor ID and day of the week")
    void existsByDoctorIdAndDayOfWeek_whenDoesNotExist_returnsFalse() {
        doctorScheduleRepo.save(DoctorScheduleRepositoryTestFactory.create(doctor, DayOfWeek.FRIDAY,
                START_TIME, END_TIME));

        var result = doctorScheduleRepo.existsByDoctorIdAndDayOfWeek(doctor.getId(), DayOfWeek.MONDAY);
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Updates START TIME and END TIME")
    void shouldUpdateStartTimeAndEndTime() {
        var saved = doctorScheduleRepo.save(DoctorScheduleRepositoryTestFactory.create(doctor,
                DayOfWeek.FRIDAY, START_TIME, END_TIME));
        saved.updateTimeRange(START_TIME.plusHours(1), END_TIME.plusHours(1));
        doctorScheduleRepo.save(saved);
        assertThat(doctorScheduleRepo.findById(saved.getId())).isPresent().get().
                extracting(DoctorSchedule::getStartTime).isEqualTo(START_TIME.plusHours(1));
    }

    @Test
    @DisplayName("Deletes doctor schedule")
    void shouldDeleteDoctorSchedule() {
        var saved = doctorScheduleRepo.save(DoctorScheduleRepositoryTestFactory.create(doctor,
                DayOfWeek.FRIDAY, START_TIME, END_TIME));
        doctorScheduleRepo.delete(saved);
        assertThat(doctorScheduleRepo.findById(saved.getId())).isEmpty();
    }
}