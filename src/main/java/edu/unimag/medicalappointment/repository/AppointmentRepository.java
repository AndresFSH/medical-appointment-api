package edu.unimag.medicalappointment.repository;

import edu.unimag.medicalappointment.domain.entity.Appointment;
import edu.unimag.medicalappointment.domain.entity.enums.AppointmentStatus;
import edu.unimag.medicalappointment.domain.projection.DoctorAppointmentCountProjection;
import edu.unimag.medicalappointment.domain.projection.OfficeOccupancyProjection;
import edu.unimag.medicalappointment.domain.projection.PatientNoShowAppointmentProjection;
import edu.unimag.medicalappointment.domain.projection.SpecialtyCountProjection;
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
        SELECT new edu.unimag.medicalappointment.domain.projection.OfficeOccupancyProjection(
            a.office.id,a.office.name,COUNT(a)
        )
        FROM Appointment a
        WHERE a.startAt BETWEEN :from AND :to
        GROUP BY a.office.id,a.office.name
    """)
    List<OfficeOccupancyProjection> calculateOfficeOccupancy(
        @Param("from") LocalDateTime from,
        @Param("to") LocalDateTime to
    );

    @Query("""
        SELECT new edu.unimag.medicalappointment.domain.projection.SpecialtyCountProjection(
            a.doctor.specialty.id,a.doctor.specialty.name,COUNT(a)
        )
        FROM Appointment a
        WHERE a.status IN :statuses
        GROUP BY a.doctor.specialty.id, a.doctor.specialty.name
    """)
    List<SpecialtyCountProjection> countBySpecialty(@Param("statuses") List<AppointmentStatus> statuses);

    @Query("""
        SELECT new edu.unimag.medicalappointment.domain.projection.DoctorAppointmentCountProjection(
            a.doctor.id,a.doctor.fullName,COUNT(a)
        )
        FROM Appointment a
        WHERE a.status = :status
        GROUP BY a.doctor.id, a.doctor.fullName
        ORDER BY COUNT(a) DESC
    """)
    List<DoctorAppointmentCountProjection> countCompletedAppointmentsByDoctor(@Param("status") AppointmentStatus status);

    @Query("""
        SELECT new edu.unimag.medicalappointment.domain.projection.PatientNoShowAppointmentProjection(
            a.patient.id,a.patient.fullName,COUNT(a)
        )
        FROM Appointment a
        WHERE a.status = :status
            AND a.startAt BETWEEN :from AND :to
        GROUP BY a.patient.id, a.patient.fullName
        ORDER BY COUNT(a) DESC
    """)
    List<PatientNoShowAppointmentProjection> countNoShowAppointmentsByPatient(@Param("status")  AppointmentStatus status,
                                                                              @Param("from") LocalDateTime from,
                                                                              @Param("to") LocalDateTime to);
}
