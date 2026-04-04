package edu.unimag.medicalappointment.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.ValidationException;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "specialties")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Specialty {

    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "specialty", nullable = false, unique = true) private String name;
    @Setter
    @OneToMany(mappedBy = "specialty", fetch = FetchType.LAZY) private List<Doctor> doctors;

    public void setName(String name) {
        this.name = normalize(name);
    }

    @PrePersist
    @PreUpdate
    public void normalizeFields() {
        this.name = normalize(this.name);
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
