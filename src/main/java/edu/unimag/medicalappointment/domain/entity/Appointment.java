package edu.unimag.medicalappointment.domain.entity;

import edu.unimag.medicalappointment.domain.entity.enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "appointments")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {

    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;

    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id",nullable = false) private Patient patient;

    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id",nullable = false) private Doctor doctor;

    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    @JoinColumn(name = "office_id",nullable = false) private Office office;

    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_type_id",nullable = false) private AppointmentType appointmentType;

    @Column(name = "startAt", nullable = false) private LocalDateTime startAt;
    @Column(name = "endAt", nullable = false) private LocalDateTime endAt;
    @Builder.Default@Enumerated(EnumType.STRING)
    @Column(name = "status",nullable = false) private AppointmentStatus status =  AppointmentStatus.SCHEDULED;
    @Column(name = "cancellation_reason") private String cancellationReason;
    @CreationTimestamp
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at") private Instant updatedAt;

}
