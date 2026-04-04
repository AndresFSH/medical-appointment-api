package edu.unimag.medicalappointment.testutil;

import edu.unimag.medicalappointment.domain.entity.Doctor;
import edu.unimag.medicalappointment.domain.entity.DoctorSchedule;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class DoctorScheduleRepositoryTestFactory {

    public static DoctorSchedule create(Doctor doctor, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        return DoctorSchedule.create(doctor, dayOfWeek, startTime, endTime);
    }

}
