package edu.unimag.medicalappointment.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "specialties")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Specialty {

    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "specialty", nullable = false, unique = true) private String name;
    @Setter
    @OneToMany(mappedBy = "specialty", fetch = FetchType.LAZY) private List<Doctor> doctors;

    public void setName(String name) {
        this.name = name.toLowerCase().trim();
    }

}
