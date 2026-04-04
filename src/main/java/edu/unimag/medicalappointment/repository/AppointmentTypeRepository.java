package edu.unimag.medicalappointment.repository;

import edu.unimag.medicalappointment.domain.entity.AppointmentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AppointmentTypeRepository extends JpaRepository<AppointmentType, UUID> {
    boolean existsByName(String name);
}
