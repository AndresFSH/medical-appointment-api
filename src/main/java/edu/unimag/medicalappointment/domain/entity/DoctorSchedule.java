package edu.unimag.medicalappointment.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.ValidationException;
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
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class DoctorSchedule {

    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false) private Doctor doctor;
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false) private DayOfWeek dayOfWeek;
    @Column(name = "start_time", nullable = false) private LocalTime startTime;
    @Column(name = "end_time", nullable = false) private LocalTime endTime;

   public static DoctorSchedule create(Doctor doctor, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
       if (doctor == null || dayOfWeek == null || startTime == null || endTime == null) {
           throw new IllegalArgumentException("Invalid data");
       }
       if(!startTime.isBefore(endTime)) {
           throw new IllegalArgumentException("StartTime must be before EndTime");
       }
       return DoctorSchedule.builder().doctor(doctor).dayOfWeek(dayOfWeek).startTime(startTime).endTime(endTime).build();
   }

    public void updateTimeRange(LocalTime newStart, LocalTime newEnd) {
        if (!newStart.isBefore(newEnd)) {
            throw new IllegalArgumentException("startTime must be before endTime");
        }
        this.startTime = newStart;
        this.endTime = newEnd;
    }

    @PrePersist @PreUpdate
    public void validate(){
        if (!startTime.isBefore(endTime)){
            throw new ValidationException("Invalid schedule");
        }
    }

}
