package edu.unimag.medicalappointment.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "appointment_type")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentType {

    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "name",  nullable = false, unique = true) private String name;
    @Setter
    @Column(name = "duration_minutes", nullable = false) private Integer durationMinutes;
    @OneToMany(mappedBy = "appointmentType", fetch = FetchType.LAZY) private List<Appointment> appointments;

    public void setName(String name){
        this.name = name.toLowerCase().trim();
    }

}
