package edu.unimag.medicalappointment.domain.entity;

import edu.unimag.medicalappointment.domain.entity.enums.PatientStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "patients")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {

    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Setter
    @Column(name = "full_name", nullable = false) private String fullName;
    @Column(name = "email",nullable = false, unique = true)private String email;
    @Setter @Builder.Default @Enumerated(EnumType.STRING) @Column(name = "status", nullable = false)
    private PatientStatus status = PatientStatus.ACTIVE;

    @OneToMany(mappedBy = "patient", fetch = FetchType.LAZY) private List<Appointment> appointments;

    public void activate(){
        this.status = PatientStatus.ACTIVE;
    }

    public void deactivate(){
        this.status = PatientStatus.INACTIVE;
    }

    public void setEmail(String email) {
        this.email = email.toLowerCase().trim();
    }

}
