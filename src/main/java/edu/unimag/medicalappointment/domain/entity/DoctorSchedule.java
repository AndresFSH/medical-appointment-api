package edu.unimag.medicalappointment.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "doctor_schedule",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_doctor_day",
                columnNames = {"doctor_id","day_of_week"}
        )
    )
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorSchedule {

    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false) private Doctor doctor;
    @Column(name = "day_of_week", nullable = false) private DayOfWeek dayOfWeek;
    @Column(name = "start_time", nullable = false) private LocalTime startTime;
    @Column(name = "end_time", nullable = false) private LocalTime endTime;

    public boolean isValidRange(){
        return startTime != null && endTime != null && startTime.isBefore(endTime);
    }

}
