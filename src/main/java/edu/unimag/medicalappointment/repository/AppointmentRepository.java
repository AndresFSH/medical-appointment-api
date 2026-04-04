package edu.unimag.medicalappointment.repository;

import edu.unimag.medicalappointment.domain.entity.Appointment;
import edu.unimag.medicalappointment.domain.entity.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    List<Appointment> findByPatientIdAndStatus(UUID patientId, AppointmentStatus status);
    List<Appointment> findByStartAtBetween(LocalDateTime from, LocalDateTime to);


    @Query("""
        SELECT CASE  WHEN COUNT(a) > 0 THEN true ELSE false END 
        FROM Appointment a
        WHERE a.doctor.id = :doctorId
            AND a.status IN :statuses
            AND a.startAt < :endAt
            AND a.endAt > :startAt
        """)
    boolean existsOverlappingDoctor(
        @Param("doctorId") UUID doctorId,
        @Param("startAt") LocalDateTime startAt,
        @Param("endAt") LocalDateTime endAt,
        @Param("statuses") List<AppointmentStatus> statuses
    );

    @Query("""
        SELECT CASE  WHEN COUNT(a) > 0 THEN true ELSE false END 
        FROM Appointment a
        WHERE a.office.id = :officeId
            AND a.status IN :statuses
            AND a.startAt < :endAt
            AND a.endAt > :startAt
    """)
    boolean existsOverlappingOffice(
        @Param("officeId") UUID officeId,
        @Param("startAt") LocalDateTime startAt,
        @Param("endAt") LocalDateTime endAt,
        @Param("statuses") List<AppointmentStatus> statuses
    );

    @Query("""
        SELECT CASE  WHEN COUNT(a) > 0 THEN true ELSE false END 
        FROM Appointment a
        WHERE a.patient.id = :patientId
            AND a.status IN :statuses
            AND a.startAt < :endAt
            AND a.endAt > :startAt
    """)
    boolean existsOverlappingPatient(
            @Param("patientId") UUID patientId,
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt,
            @Param("statuses") List<AppointmentStatus> statuses
    );

    @Query("""
        SELECT a FROM Appointment a
        WHERE a.doctor.id = :doctorId
            AND a.status IN :statuses
            AND CAST(a.startAt AS LocalDate) = :date
    """)
    List<Appointment> findByDoctorIdAndDate(@Param("doctorId") UUID doctorId,
                                            @Param("date") LocalDate date,
                                            @Param("statuses")  List<AppointmentStatus> statuses);

    @Query("""
        SELECT a.office.id, COUNT(a)
        FROM Appointment a
        WHERE a.startAt BETWEEN :from AND :to
        GROUP BY a.office.id
    """)
    List<Object[]> calculateOfficeOccupancy(
        @Param("from") LocalDateTime from,
        @Param("to") LocalDateTime to
    );

    @Query("""
        SELECT a.doctor.specialty.id,COUNT(a) 
        FROM Appointment a
        WHERE a.status IN :statuses
        GROUP BY a.doctor.specialty.id
    """)
    List<Object[]> countBySpecialty(@Param("statuses") List<AppointmentStatus> statuses);

    @Query("""
        SELECT a.doctor.id, COUNT(a)
        FROM Appointment a
        WHERE a.status = :status
        GROUP BY a.doctor.id
        ORDER BY COUNT(a) DESC
    """)
    List<Object[]> countCompletedAppointmentsByDoctor(@Param("status") AppointmentStatus status);

    @Query("""
        SELECT a.patient.id, COUNT(a)
        FROM Appointment a
        WHERE a.status = :status
            AND a.startAt BETWEEN :from AND :to
        GROUP BY a.patient.id
        ORDER BY COUNT(a) DESC
    """)
    List<Object[]> countNoShowAppointmentsByPatient(@Param("status")  AppointmentStatus status,
                                                    @Param("from") LocalDateTime from,
                                                    @Param("to") LocalDateTime to);
}
