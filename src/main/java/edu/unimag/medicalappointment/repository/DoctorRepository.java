package edu.unimag.medicalappointment.repository;

import edu.unimag.medicalappointment.domain.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DoctorRepository  extends JpaRepository<Doctor, UUID> {

    List<Doctor> findBySpecialtyIdAndActiveTrue(UUID specialtyId);

}
