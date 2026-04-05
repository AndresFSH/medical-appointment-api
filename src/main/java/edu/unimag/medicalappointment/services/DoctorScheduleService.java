package edu.unimag.medicalappointment.services;

import edu.unimag.medicalappointment.dto.DoctorScheduleDTO.*;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

public interface DoctorScheduleService {

    DoctorScheduleResponse createDoctorSchedule(UUID doctorId, CreateDoctorScheduleRequest req);
    DoctorScheduleResponse findByDoctorIdAndDayOfWeek(UUID doctorId, DayOfWeek dayOfWeek);
    List<DoctorScheduleResponse> findAll(UUID doctorId);

}
