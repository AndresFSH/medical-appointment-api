package edu.unimag.medicalappointment.repository;

import edu.unimag.medicalappointment.domain.entity.Specialty;
import edu.unimag.medicalappointment.testutil.SpecialtyRepositoryTestFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class SpecialtyRepositoryTest extends AbstractRepositoryIT {

    @Autowired SpecialtyRepository specialtyRepo;

    @Test
    @DisplayName("Returns a specialty with a given id")
    void shouldCreateAndFindById(){
        var saved = specialtyRepo.save(SpecialtyRepositoryTestFactory.create());
        var result = specialtyRepo.findById(saved.getId());
        assertThat(result).isPresent().get().extracting(Specialty::getId).isEqualTo(saved.getId());
    }

    @Test
    @DisplayName("Returns true if exists a specialty with a given name")
    void existsByName_whenExists_returnsTrue() {
        specialtyRepo.save(SpecialtyRepositoryTestFactory.create("name"));
        var result = specialtyRepo.existsByName("name");
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Returns false if does not exist a specialty with a given name")
    void existsByName_whenDoesNotExist_returnsFalse() {
        specialtyRepo.save(SpecialtyRepositoryTestFactory.create("name"));
        var result = specialtyRepo.existsByName("other");
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Updates a specialty's name")
    void shouldUpdateName(){
        var saved = specialtyRepo.save(SpecialtyRepositoryTestFactory.create("name"));
        saved.setName("newName");
        specialtyRepo.save(saved);
        var result = specialtyRepo.findById(saved.getId());
        assertThat(result).isPresent().get().extracting(Specialty::getName).isEqualTo("newName".toLowerCase().trim());
    }

    @Test
    @DisplayName("Deletes a specialty")
    void shouldDelete(){
        var saved = specialtyRepo.save(SpecialtyRepositoryTestFactory.create());
        specialtyRepo.delete(saved);
        var result = specialtyRepo.findById(saved.getId());
        assertThat(result).isEmpty();
    }
}