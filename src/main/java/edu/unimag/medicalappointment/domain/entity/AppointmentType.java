package edu.unimag.medicalappointment.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.ValidationException;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "appointment_type")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AppointmentType {

    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "name",  nullable = false, unique = true) private String name;
    @Setter
    @Column(name = "duration_minutes", nullable = false) private Integer durationMinutes;
    @OneToMany(mappedBy = "appointmentType", fetch = FetchType.LAZY) private List<Appointment> appointments;

    public void setName(String name){
        this.name = normalize(name);
    }

    @PrePersist
    @PreUpdate
    public void validate(){
        this.name = normalize(this.name);
        if(this.durationMinutes == null||this.durationMinutes <= 0){
            throw  new ValidationException("duration_minutes must be positive");
        }
    }

    private static String normalize(String value) {
        if (value == null) {
            throw new ValidationException("name must not be null");
        }
        String normalized = value.trim().toLowerCase();
        if (normalized.isEmpty()) {
            throw new ValidationException("name must not be blank");
        }
        return normalized;
    }

}
