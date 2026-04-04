package edu.unimag.medicalappointment.repository;

import edu.unimag.medicalappointment.domain.entity.DoctorSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.Optional;
import java.util.UUID;

public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, UUID> {

    Optional<DoctorSchedule> findByDoctorIdAndDayOfWeek(UUID doctorId, DayOfWeek dayOfWeek);
    boolean existsByDoctorIdAndDayOfWeek(UUID doctorId, DayOfWeek dayOfWeek);

}
