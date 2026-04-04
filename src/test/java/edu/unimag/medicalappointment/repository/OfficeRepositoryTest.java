package edu.unimag.medicalappointment.repository;

import edu.unimag.medicalappointment.domain.entity.Office;
import edu.unimag.medicalappointment.domain.entity.enums.OfficeStatus;
import edu.unimag.medicalappointment.testutil.OfficeRepositoryTestFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class OfficeRepositoryTest extends AbstractRepositoryIT {

    @Autowired OfficeRepository officeRepo;

    @Test
    @DisplayName("Returns an office with a given id")
    void shouldCreateAndFindById(){
        var saved = officeRepo.save(OfficeRepositoryTestFactory.create("name"));
        var result = officeRepo.findById(saved.getId());
        assertThat(result).isPresent().get().extracting(Office::getId).isEqualTo(saved.getId());
    }

    @Test
    @DisplayName("Returns true if exists an office with a given name")
    void existsByName_whenExists_returnsTrue() {
        var saved = officeRepo.save(OfficeRepositoryTestFactory.create("name"));
        var result = officeRepo.existsByName("name");
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Returns false if does not exist an office with a given name")
    void existsByName_whenDoesNotExist_returnsFalse() {
        officeRepo.save(OfficeRepositoryTestFactory.create("name"));
        var result = officeRepo.existsByName("other");
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Updates office availability")
    void shouldUpdateOfficeAvailability(){
        var saved = officeRepo.save(OfficeRepositoryTestFactory.create("name", "location",
                OfficeStatus.UNAVAILABLE));
        saved.setAvailable();
        officeRepo.save(saved);

        var result = officeRepo.findById(saved.getId());
        assertThat(result).isPresent().get().extracting(Office::getStatus).isEqualTo(OfficeStatus.AVAILABLE);
    }
    @Test
    @DisplayName("Deletes an office ")
    void shouldDeleteOffice(){
        var saved = officeRepo.save(OfficeRepositoryTestFactory.create("name"));
        officeRepo.delete(saved);
        var result = officeRepo.findById(saved.getId());
        assertThat(result).isEmpty();
    }
}