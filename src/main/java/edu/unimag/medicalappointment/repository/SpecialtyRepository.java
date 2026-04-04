package edu.unimag.medicalappointment.repository;

import edu.unimag.medicalappointment.domain.entity.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpecialtyRepository extends JpaRepository<Specialty, UUID> {

    boolean existsByName(String name);

}
