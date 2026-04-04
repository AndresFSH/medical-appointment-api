package edu.unimag.medicalappointment.repository;

import edu.unimag.medicalappointment.domain.entity.Office;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OfficeRepository extends JpaRepository<Office, UUID> {

    boolean existsByName(String name);

}
