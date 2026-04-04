package edu.unimag.medicalappointment.domain.entity;

import edu.unimag.medicalappointment.domain.entity.enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.UUID;

@Entity
@Table(name = "appointments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
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

    @Column(name = "start_at", nullable = false) private LocalDateTime startAt;
    @Column(name = "end_at", nullable = false) private LocalDateTime endAt;

    @Builder.Default
    @Setter(AccessLevel.PRIVATE)
    @Enumerated(EnumType.STRING)
    @Column(name = "status",nullable = false)
    private AppointmentStatus status = AppointmentStatus.SCHEDULED;

    @Setter(AccessLevel.PRIVATE)
    @Column(name = "cancellation_reason") private String cancellationReason;

    @Setter(AccessLevel.PRIVATE)
    @Column(name = "observations") private String observations;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at") private Instant updatedAt;

    public static Appointment schedule(Patient patient, Doctor doctor, Office office,
                                       AppointmentType type, LocalDateTime startAt) {
        if(type == null) {
            throw new IllegalArgumentException("Appointment type is required");
        }
        if (patient == null || doctor == null || office == null) {
            throw new IllegalArgumentException("Patient, doctor and office are required");
        }
        if (startAt == null) {
            throw new IllegalArgumentException("startAt is required");
        }

        LocalDateTime endAt = startAt.plusMinutes(type.getDurationMinutes());

        return Appointment.builder().patient(patient).doctor(doctor).office(office)
                .appointmentType(type).startAt(startAt).endAt(endAt).build();
    }

    public void confirm(){
        if(this.status != AppointmentStatus.SCHEDULED){
            throw  new IllegalStateException("Only SCHEDULED appointments can be confirmed." +
                    " Current status:"+this.status);
        }
        this.status = AppointmentStatus.CONFIRMED;
    }

    public void cancel(String reason){
        if(reason == null || reason.isBlank()){
            throw new IllegalArgumentException("Reason cannot be null or empty");
        }
        if(!EnumSet.of(AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED).contains(this.status)){
            throw new IllegalStateException("Only SCHEDULED or CONFIRMED appointments can be cancelled." +
                    " Current status: "+this.status);
        }
        this.status = AppointmentStatus.CANCELLED;
        this.cancellationReason = reason;
    }

    public void complete(String observations){
        if(this.status != AppointmentStatus.CONFIRMED){
            throw new IllegalStateException("Only CONFIRMED appointments can be completed." +
                    " Current status: "+this.status);
        }
        this.status = AppointmentStatus.COMPLETED;
        this.observations = observations;
    }

    public void markNoShow(){
        if(this.status != AppointmentStatus.CONFIRMED){
            throw new IllegalStateException("Only CONFIRMED appointments can be marked as NO SHOW." +
                    " Current status: "+this.status);
        }
        this.status = AppointmentStatus.NO_SHOW;
    }

    @PrePersist
    @PreUpdate
    public void validate(){
        if(startAt != null && endAt != null && !startAt.isBefore(endAt)){
            throw new IllegalStateException("Start date must be before end date.");
        }
        if(appointmentType != null && startAt != null && endAt != null){
            LocalDateTime expectedEnd = startAt.plusMinutes(appointmentType.getDurationMinutes());
            if(!expectedEnd.equals(endAt)){
                throw new IllegalStateException("EndAt does not match appointmentType duration.");
            }
        }
    }


}
