package edu.unimag.medicalappointment.domain.entity;

import edu.unimag.medicalappointment.domain.entity.enums.OfficeStatus;
import jakarta.persistence.*;
import jakarta.validation.ValidationException;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "offices")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Office {

    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "name", nullable = false, unique = true) private String name;
    @Column(name = "location", nullable = false, unique = true) private String location;
    @Setter(AccessLevel.PRIVATE) @Builder.Default @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false) private OfficeStatus status = OfficeStatus.AVAILABLE;
    @OneToMany(mappedBy = "office", fetch = FetchType.LAZY)  private List<Appointment> appointments;



    public void setAvailable() {
        this.status = OfficeStatus.AVAILABLE;
    }

    public void setUnavailable() {
        this.status = OfficeStatus.UNAVAILABLE;
    }

    public void setInactive() {
        this.status = OfficeStatus.INACTIVE;
    }

    public void setName(String name) {
        this.name = normalize("name", name);
    }

    public void setLocation(String location) {
        this.location = normalize("location", location);
    }

    @PrePersist
    @PreUpdate
    public void normalizeFields() {
        this.name = normalize("name", this.name);
        this.location = normalize("location", this.location);
    }

    private static String normalize(String field, String value) {
        if (value == null) {
            throw new ValidationException(field + " must not be null");
        }
        String normalized = value.trim().toLowerCase();
        if (normalized.isEmpty()) {
            throw new ValidationException(field + " must not be blank");
        }
        return normalized;
    }
}
