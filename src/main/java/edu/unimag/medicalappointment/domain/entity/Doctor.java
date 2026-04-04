package edu.unimag.medicalappointment.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "doctors")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Doctor {

    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "full_name", nullable = false) private String fullName;
    @Builder.Default
    @Column(name = "active", nullable = false) private boolean active = true;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "specialty_id", nullable = false) private Specialty specialty;

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

}
