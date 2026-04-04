package edu.unimag.medicalappointment.repository;

import edu.unimag.medicalappointment.domain.entity.*;
import edu.unimag.medicalappointment.domain.entity.enums.AppointmentStatus;
import edu.unimag.medicalappointment.repository.projection.SpecialtyCountProjection;
import edu.unimag.medicalappointment.testutil.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AppointmentRepositoryTest extends AbstractRepositoryIT {

    @Autowired AppointmentRepository appointmentRepo;
    @Autowired SpecialtyRepository specialtyRepo;
    @Autowired DoctorRepository doctorRepo;
    @Autowired AppointmentTypeRepository appointmentTypeRepo;
    @Autowired PatientRepository patientRepo;
    @Autowired OfficeRepository officeRepo;


    private static final LocalDateTime BASE = LocalDateTime.of(2026,5,16,10,50);

    private Patient patient;
    private Doctor doctor;
    private Office office;
    private AppointmentType appointmentType;
    private final List<AppointmentStatus> ACTIVE_STATUSES =
            List.of(AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED);

    @BeforeEach
    void setUp() {
        patient = patientRepo.save(PatientRepositoryTestFactory.create());
        Specialty specialty = specialtyRepo.save(SpecialtyRepositoryTestFactory.create());
        doctor = doctorRepo.save(DoctorRepositoryTestFactory.create(specialty));
        office = officeRepo.save(OfficeRepositoryTestFactory.create("office"));
        appointmentType = appointmentTypeRepo.save(AppointmentTypeRepositoryTestFactory.create());
    }

    @Test
    @DisplayName("Returns an appointment with a given id")
    void shouldCreateAndFindById(){
        //When
        var saved = appointmentRepo.save(AppointmentRepositoryTestFactory.scheduledAppointment(patient,
                                        doctor,office,appointmentType, BASE));

        //Then
        assertThat(appointmentRepo.findById(saved.getId())).isPresent().get()
                .extracting(Appointment::getId).isEqualTo(saved.getId());

    }

    @Test
    @DisplayName("returns a list of appointments with a given status for a patient")
    void shouldFindByPatientIdAndStatus() {

        appointmentRepo.save(AppointmentRepositoryTestFactory.scheduledAppointment(patient,
                                        doctor,office,appointmentType, BASE));
        appointmentRepo.save(AppointmentRepositoryTestFactory.cancelledAppointmentFromScheduled
                (patient,doctor,office,appointmentType,BASE.plusDays(10),"reason"));

        //When
        var result = appointmentRepo.findByPatientIdAndStatus(patient.getId(),AppointmentStatus.SCHEDULED);

        //Then
        assertThat(result).hasSize(1);
        assertThat(result).extracting(a -> a.getPatient().getId()).containsOnly(patient.getId());
        assertThat(result).extracting(Appointment::getStatus).containsOnly(AppointmentStatus.SCHEDULED);
    }

    @Test
    @DisplayName("Returns a list of appointments in a date range")
    void shouldFindByStartAtBetween() {
        var inside = BASE.plusDays(10);
        var outside = BASE.plusMonths(1);
        var from = BASE.minusDays(1);
        var to = BASE.plusDays(15);

        appointmentRepo.save(AppointmentRepositoryTestFactory.scheduledAppointment(patient,
                doctor,office,appointmentType, inside));
        appointmentRepo.save(AppointmentRepositoryTestFactory.scheduledAppointment(patient,
                doctor,office,appointmentType,outside));

        //When
        var result = appointmentRepo.findByStartAtBetween(from, to);
        //Then
        assertThat(result).hasSize(1).allSatisfy(a -> assertThat(
                a.getStartAt()).isBetween(from, to));
    }

    @Test
    @DisplayName("Returns true if there is an appointment overlap for a doctor")
    void existsOverlappingDoctor_whenOverlapExists_ReturnsTrue() {
        appointmentRepo.save(AppointmentRepositoryTestFactory.scheduledAppointment(patient,doctor,
                office,appointmentType, BASE));

        //When -> Then
        assertThat(appointmentRepo.existsOverlappingDoctor(doctor.getId(),BASE.minusMinutes(30),
                BASE.plusMinutes(30),ACTIVE_STATUSES)).isTrue();
    }

    @Test
    @DisplayName("Returns false if there is no appointment overlap for a doctor")
    void existsOverlappingDoctor_whenNoOverlap_ReturnsFalse() {
        appointmentRepo.save(AppointmentRepositoryTestFactory.scheduledAppointment(patient,doctor,
                office,appointmentType, BASE));

        //When -> Then
        assertThat(appointmentRepo.existsOverlappingDoctor(doctor.getId(),BASE.plusHours(1),
                BASE.plusHours(2),ACTIVE_STATUSES)).isFalse();
    }

    @Test
    @DisplayName("existsOverlappingDoctor ignores cancelled appointments")
    void existsOverlappingDoctor_ignoresCancelledAppointments() {
        appointmentRepo.save(AppointmentRepositoryTestFactory.cancelledAppointmentFromConfirmed(patient,
                doctor,office,appointmentType, BASE, "reason"));

        //When -> Then
        assertThat(appointmentRepo.existsOverlappingDoctor(doctor.getId(),BASE,
                BASE.plusHours(1),ACTIVE_STATUSES)).isFalse();
    }

    @Test
    @DisplayName("Returns true if there is an appointment overlap for an office")
    void existsOverlappingOffice_whenOverlapExists_ReturnsTrue() {
        appointmentRepo.save(AppointmentRepositoryTestFactory.scheduledAppointment(patient,doctor,
                office,appointmentType,BASE));

        assertThat(appointmentRepo.existsOverlappingOffice(office.getId(),BASE.minusMinutes(30),
                BASE.plusMinutes(30),ACTIVE_STATUSES)).isTrue();
    }

    @Test
    @DisplayName("Returns false if there is no appointment overlap for an office")
    void existsOverlappingOffice_whenNoOverlap_ReturnsFalse() {
        appointmentRepo.save(AppointmentRepositoryTestFactory.scheduledAppointment(patient,doctor,
                office,appointmentType,BASE));

        assertThat(appointmentRepo.existsOverlappingOffice(office.getId(),BASE.plusHours(1),
                BASE.plusHours(2),ACTIVE_STATUSES)).isFalse();
    }

    @Test
    @DisplayName("existsOverlappingOffice ignores cancelled appointments")
    void existsOverlappingOffice_ignoresCancelledAppointments() {
        appointmentRepo.save(AppointmentRepositoryTestFactory.cancelledAppointmentFromConfirmed(patient,doctor,
                office,appointmentType,BASE, "reason"));

        assertThat(appointmentRepo.existsOverlappingOffice(office.getId(),BASE.minusMinutes(30),
                BASE.plusMinutes(30),ACTIVE_STATUSES)).isFalse();
    }

    @Test
    @DisplayName("Returns true if there is an appointment overlap for a patient")
    void existsOverlappingPatient_whenOverlapExists_ReturnsTrue() {
        appointmentRepo.save(AppointmentRepositoryTestFactory.scheduledAppointment(patient,doctor,
                office,appointmentType,BASE));

        assertThat(appointmentRepo.existsOverlappingPatient(patient.getId(),BASE.minusMinutes(30),
                BASE.plusMinutes(30),ACTIVE_STATUSES)).isTrue();
    }

    @Test
    @DisplayName("Returns false if there is no appointment overlap for a patient")
    void existsOverlappingPatient_whenNoOverlap_ReturnsFalse() {
        appointmentRepo.save(AppointmentRepositoryTestFactory.scheduledAppointment(patient,doctor,
                office,appointmentType,BASE));

        assertThat(appointmentRepo.existsOverlappingPatient(patient.getId(),BASE.plusHours(1),
                BASE.plusHours(2),ACTIVE_STATUSES)).isFalse();
    }

    @Test
    @DisplayName("existsOverlappingPatients ignores cancelled appointments")
    void existsOverlappingPatient_ignoresCancelledAppointments() {
        appointmentRepo.save(AppointmentRepositoryTestFactory.cancelledAppointmentFromScheduled(patient,doctor,
                office,appointmentType,BASE, "reason"));

        assertThat(appointmentRepo.existsOverlappingPatient(patient.getId(),BASE.minusMinutes(30),
                BASE.plusMinutes(30),ACTIVE_STATUSES)).isFalse();
    }

    @Test
    @DisplayName("Returns a list of appointments by doctor in a date range")
    void shouldFindByDoctorIdAndDate() {
        appointmentRepo.save(AppointmentRepositoryTestFactory.scheduledAppointment(patient, doctor,
                office, appointmentType, BASE));

        var result = appointmentRepo.findByDoctorIdAndDate(doctor.getId(), BASE.toLocalDate(),ACTIVE_STATUSES);

        assertThat(result).hasSize(1);
        assertThat(result).extracting(a -> a.getDoctor().getId()).containsOnly(doctor.getId());
        assertThat(result).extracting(Appointment::getStartAt).containsOnly(BASE);
    }

    @Test
    @DisplayName("Returns a list of offices with their daily occupancy")
    void shouldCalculateOfficeOccupancy() {
        appointmentRepo.save(AppointmentRepositoryTestFactory.scheduledAppointment(patient, doctor,
                office, appointmentType, BASE));
        appointmentRepo.save(AppointmentRepositoryTestFactory.scheduledAppointment(patient, doctor,
                office, appointmentType, BASE.plusHours(1)));
        appointmentRepo.save(AppointmentRepositoryTestFactory.scheduledAppointment(patient, doctor,
                office, appointmentType, BASE.plusHours(2)));

        var result = appointmentRepo.calculateOfficeOccupancy(BASE, BASE.plusDays(1));

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().appointmentCount()).isEqualTo(3L);
        assertThat(result.getFirst().officeName()).isEqualTo("office");
    }

    @Test
    @DisplayName("Returns a list of specialties with a count of no-show or cancelled appointments")
    void shouldCountBySpecialty() {
        var saved = appointmentRepo.save(AppointmentRepositoryTestFactory.cancelledAppointmentFromScheduled(patient, doctor,
                office, appointmentType, BASE,"reason"));
        appointmentRepo.save(AppointmentRepositoryTestFactory.cancelledAppointmentFromConfirmed(patient, doctor,
                office, appointmentType, BASE.plusHours(1), "reason"));

        var otherSpecialty = specialtyRepo.save(SpecialtyRepositoryTestFactory.create("other"));
        var otherDoctor =  doctorRepo.save(DoctorRepositoryTestFactory.create(otherSpecialty));
        appointmentRepo.save(AppointmentRepositoryTestFactory.noShowAppointment(patient, otherDoctor,
                office, appointmentType, BASE.plusHours(2)));

        var result = appointmentRepo.countBySpecialty(List.of(AppointmentStatus.CANCELLED, AppointmentStatus.NO_SHOW));
        assertThat(result).hasSize(2);
        long total = result.stream().mapToLong(SpecialtyCountProjection::appointmentCount).sum();
        assertThat(total).isEqualTo(3L);
        UUID expectedSpecialtyId = doctor.getSpecialty().getId();
        long countForMainSpecialty = result.stream().filter(p -> p.specialtyId().equals(expectedSpecialtyId))
                .mapToLong(SpecialtyCountProjection::appointmentCount).findFirst().orElse(0L);
        assertThat(countForMainSpecialty).isEqualTo(2L);
    }

    @Test
    @DisplayName("Returns a list of doctors ranked by completed appointments")
    void shouldCountCompletedAppointmentsByDoctor() {
        appointmentRepo.save(AppointmentRepositoryTestFactory.completedAppointment(patient, doctor,
                office, appointmentType, BASE, "obs"));
        appointmentRepo.save(AppointmentRepositoryTestFactory.completedAppointment(patient, doctor,
                office, appointmentType, BASE.plusHours(2), "obs2"));
        appointmentRepo.save(AppointmentRepositoryTestFactory.noShowAppointment(patient, doctor,
                office, appointmentType, BASE.minusHours(2)));

        var otherSpecialty = specialtyRepo.save(SpecialtyRepositoryTestFactory.create("other"));
        var otherDoctor = doctorRepo.save(DoctorRepositoryTestFactory.create(otherSpecialty));

        appointmentRepo.save(AppointmentRepositoryTestFactory.completedAppointment(patient, otherDoctor,
                office, appointmentType, BASE.plusHours(2), "obs3"));

        var result = appointmentRepo.countCompletedAppointmentsByDoctor(AppointmentStatus.COMPLETED);

        assertThat(result).hasSize(2);

        assertThat(result.getFirst().doctorId()).isEqualTo(doctor.getId());
        assertThat((Long) result.getFirst().appointmentCount()).isEqualTo(2L);

        assertThat(result.get(1).doctorId()).isEqualTo(otherDoctor.getId());
        assertThat((Long) result.get(1).appointmentCount()).isEqualTo(1L);
        assertThat(result.getFirst().doctorName()).isEqualTo(doctor.getFullName());
    }

    @Test
    @DisplayName("Returns a list of patients ranked by no-show appointments")
    void shouldCountNoShowAppointmentsByPatient() {

        appointmentRepo.save(AppointmentRepositoryTestFactory.noShowAppointment(patient, doctor,
                office, appointmentType, BASE));
        appointmentRepo.save(AppointmentRepositoryTestFactory.noShowAppointment(patient, doctor,
                office, appointmentType, BASE.plusHours(2)));
        appointmentRepo.save(AppointmentRepositoryTestFactory.scheduledAppointment(patient, doctor,
                office, appointmentType, BASE.plusDays(5)));

        var otherPatient = patientRepo.save(PatientRepositoryTestFactory.create());
        appointmentRepo.save(AppointmentRepositoryTestFactory.noShowAppointment(otherPatient, doctor,
                office, appointmentType, BASE));
        appointmentRepo.save(AppointmentRepositoryTestFactory.scheduledAppointment(otherPatient, doctor,
                office, appointmentType, BASE.plusDays(5)));

        var from = BASE.minusMonths(1);
        var to = BASE.plusMonths(1);

        var result = appointmentRepo.countNoShowAppointmentsByPatient(AppointmentStatus.NO_SHOW, from, to);

        assertThat(result).hasSize(2);

        assertThat(result.getFirst().patientId()).isEqualTo(patient.getId());
        assertThat((Long) result.getFirst().appointmentCount()).isEqualTo(2L);

        assertThat(result.get(1).patientId()).isEqualTo(otherPatient.getId());
        assertThat((Long) result.get(1).appointmentCount()).isEqualTo(1L);
        assertThat(result.getFirst().patientName()).isEqualTo(patient.getFullName());

    }

    @Test
    @DisplayName("Update the status")
    void shouldUpdateAppointmentStatus(){
        var saved = appointmentRepo.save(AppointmentRepositoryTestFactory.scheduledAppointment(patient, doctor,
                office, appointmentType, BASE));

        saved.confirm();
        appointmentRepo.save(saved);

        assertThat(appointmentRepo.findById(saved.getId())).isPresent().get().extracting(Appointment::getStatus)
                .isEqualTo(AppointmentStatus.CONFIRMED);
    }

    @Test
    @DisplayName("Delete an appointment")
    void shouldDeleteAppointment(){
        var saved = appointmentRepo.save(AppointmentRepositoryTestFactory.scheduledAppointment(patient,doctor,
                office, appointmentType, BASE));
        appointmentRepo.delete(saved);

        assertThat(appointmentRepo.findById(saved.getId())).isEmpty();
    }

}