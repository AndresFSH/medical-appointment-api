package edu.unimag.medicalappointment.repository;

import edu.unimag.medicalappointment.domain.entity.AppointmentType;
import edu.unimag.medicalappointment.testutil.AppointmentTypeRepositoryTestFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class AppointmentTypeRepositoryTest extends AbstractRepositoryIT{

    @Autowired AppointmentTypeRepository appointmentTypeRepo;

    @Test
    @DisplayName("Returns an appointment type with a given id")
    void shouldCreateAndFindById(){
        var saved = appointmentTypeRepo.save(AppointmentTypeRepositoryTestFactory.create());
        assertThat(appointmentTypeRepo.findById(saved.getId())).isPresent().get().
                extracting(AppointmentType::getId).isEqualTo(saved.getId());
    }

    @Test
    @DisplayName("Returns true if there is an appointment type with a given name")
    void existsByName_whenExists_returnsTrue(){
        var saved = appointmentTypeRepo.save(AppointmentTypeRepositoryTestFactory.create("type",60));
        assertThat(appointmentTypeRepo.existsByName("type")).isTrue();
    }

    @Test
    @DisplayName("Returns false if there is no appointment type with a given name")
    void existsByName_whenDoesNotExist_returnsFalse(){
        appointmentTypeRepo.save(AppointmentTypeRepositoryTestFactory.create("type",60));
        assertThat(appointmentTypeRepo.existsByName("other")).isFalse();
    }

    @Test
    @DisplayName("Update appointment type duration")
    void shouldUpdateDuration(){
        var saved = appointmentTypeRepo.save(AppointmentTypeRepositoryTestFactory.create());
        saved.setDurationMinutes(45);
        appointmentTypeRepo.save(saved);

        assertThat(appointmentTypeRepo.findById(saved.getId())).isPresent().get().
                extracting(AppointmentType::getDurationMinutes).isEqualTo(45);
    }

    @Test
    @DisplayName("Delete appointment type")
    void shouldDelete(){
        var saved = appointmentTypeRepo.save(AppointmentTypeRepositoryTestFactory.create());
        appointmentTypeRepo.delete(saved);
        assertThat(appointmentTypeRepo.findById(saved.getId())).isEmpty();
    }

}
