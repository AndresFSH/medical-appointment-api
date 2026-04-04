package edu.unimag.medicalappointment.repository;

import edu.unimag.medicalappointment.domain.entity.Patient;
import edu.unimag.medicalappointment.domain.entity.enums.PatientStatus;
import edu.unimag.medicalappointment.testutil.PatientRepositoryTestFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PatientRepositoryTest extends AbstractRepositoryIT {

    @Autowired PatientRepository patientRepo;

    @Test
    @DisplayName("Returns a patient with a given id")
    void shouldCreateAndFindById(){
        var saved = patientRepo.save(PatientRepositoryTestFactory.create());
        var result = patientRepo.findById(saved.getId());
        assertThat(result).isPresent().get().extracting(Patient::getId).isEqualTo(saved.getId());
    }

    @Test
    @DisplayName("Returns true if exists a patient with a given email")
    void existsByEmail_whenExists_ReturnsTrue() {
        patientRepo.save(PatientRepositoryTestFactory.create("name","patient@mail.com",
                PatientStatus.ACTIVE));
        var result = patientRepo.existsByEmail("patient@mail.com");
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Returns false if does not exist a patient with a given email")
    void existsByEmail_whenDoesNotExist_ReturnsFalse() {
        patientRepo.save(PatientRepositoryTestFactory.create("name","patient@mail.com",
                PatientStatus.ACTIVE));
        var result = patientRepo.existsByEmail("other@mail.com");
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Updates patient status")
    void shouldUpdatePatientStatus(){
        var saved = patientRepo.save(PatientRepositoryTestFactory.create("patient",
                "patient@mail.com", PatientStatus.INACTIVE));
        saved.activate();
        patientRepo.save(saved);
        var result = patientRepo.findById(saved.getId());
        assertThat(result).isPresent().get().extracting(Patient::getStatus).isEqualTo(PatientStatus.ACTIVE);
    }

    @Test
    @DisplayName("Deletes a patient")
    void shouldDeletePatient(){
        var saved = patientRepo.save(PatientRepositoryTestFactory.create());
        patientRepo.delete(saved);
        var result = patientRepo.findById(saved.getId());
        assertThat(result).isEmpty();
    }
}