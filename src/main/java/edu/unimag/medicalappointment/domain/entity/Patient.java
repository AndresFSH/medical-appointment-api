package edu.unimag.medicalappointment.domain.entity;

import edu.unimag.medicalappointment.domain.entity.enums.PatientStatus;
import jakarta.persistence.*;
import jakarta.validation.ValidationException;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "patients")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Patient {

    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Setter
    @Column(name = "full_name", nullable = false) private String fullName;
    @Column(name = "email",nullable = false, unique = true)private String email;
    @Setter(AccessLevel.PRIVATE) @Builder.Default @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false) private PatientStatus status = PatientStatus.ACTIVE;

    @OneToMany(mappedBy = "patient", fetch = FetchType.LAZY) private List<Appointment> appointments;

    public void activate(){
        this.status = PatientStatus.ACTIVE;
    }

    public void deactivate(){
        this.status = PatientStatus.INACTIVE;
    }

    public void setEmail(String email) {
        this.email = normalize(email);
    }

    @PrePersist
    @PreUpdate
    public void normalizeFields() {
        this.email = normalize(this.email);
    }

    private static String normalize(String value) {
        if (value == null) {
            throw new ValidationException("email must not be null");
        }
        String normalized = value.trim().toLowerCase();
        if (normalized.isEmpty()) {
            throw new ValidationException("email must not be blank");
        }
        return normalized;
    }

}
