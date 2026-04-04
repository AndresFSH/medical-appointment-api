package edu.unimag.medicalappointment.repository;

import edu.unimag.medicalappointment.domain.entity.Doctor;
import edu.unimag.medicalappointment.testutil.DoctorRepositoryTestFactory;
import edu.unimag.medicalappointment.testutil.SpecialtyRepositoryTestFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class DoctorRepositoryTest extends AbstractRepositoryIT {

    @Autowired DoctorRepository doctorRepo;
    @Autowired SpecialtyRepository specialtyRepo;

    @Test
    @DisplayName("Returns a doctor with a given id")
    void shouldCreateAndFindById(){
        var specialty = specialtyRepo.save(SpecialtyRepositoryTestFactory.create());
        var saved = doctorRepo.save(DoctorRepositoryTestFactory.create(specialty));

        var result = doctorRepo.findById(saved.getId());
        assertThat(result).isPresent().get().extracting(Doctor::getId).isEqualTo(saved.getId());
    }

    @Test
    @DisplayName("Returns a list of active doctors with a given specialty")
    void shouldFindBySpecialtyIdAndActiveTrue() {
        var specialty = specialtyRepo.save(SpecialtyRepositoryTestFactory.create());
        var saved = doctorRepo.save(DoctorRepositoryTestFactory.create(specialty));

        var result = doctorRepo.findBySpecialtyIdAndActiveTrue(saved.getSpecialty().getId());
        assertThat(result).hasSize(1);
        assertThat(result).extracting(d -> d.getSpecialty().getId()).contains(specialty.getId());
        assertThat(result).extracting(Doctor::isActive).containsOnly(true);
    }

    @Test
    @DisplayName("Update a doctor's active status")
    void shouldUpdateActive(){
        var specialty = specialtyRepo.save(SpecialtyRepositoryTestFactory.create());
        var saved = doctorRepo.save(DoctorRepositoryTestFactory.create("doctor",false,specialty));

        saved.activate();
        doctorRepo.save(saved);

        assertThat(doctorRepo.findById(saved.getId())).isPresent().get().
                extracting(Doctor::isActive).isEqualTo(true);
    }

    @Test
    @DisplayName("Delete a doctor")
    void shouldDelete(){
        var specialty = specialtyRepo.save(SpecialtyRepositoryTestFactory.create());
        var saved = doctorRepo.save(DoctorRepositoryTestFactory.create(specialty));

        doctorRepo.delete(saved);
        assertThat(doctorRepo.findById(saved.getId())).isEmpty();
    }
}