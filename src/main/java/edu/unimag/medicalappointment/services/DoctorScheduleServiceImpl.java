package edu.unimag.medicalappointment.services;

import edu.unimag.medicalappointment.domain.entity.Doctor;
import edu.unimag.medicalappointment.domain.entity.DoctorSchedule;
import edu.unimag.medicalappointment.dto.DoctorScheduleDTO.*;
import edu.unimag.medicalappointment.exception.ConflictException;
import edu.unimag.medicalappointment.exception.ResourceNotFoundException;
import edu.unimag.medicalappointment.mapper.DoctorScheduleMapper;
import edu.unimag.medicalappointment.repository.DoctorRepository;
import edu.unimag.medicalappointment.repository.DoctorScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DoctorScheduleServiceImpl implements DoctorScheduleService {

    private final DoctorScheduleRepository doctorScheduleRepo;
    private final DoctorRepository doctorRepo;
    private final DoctorScheduleMapper doctorScheduleMapper;

    @Override
    @Transactional
    public DoctorScheduleResponse createDoctorSchedule(UUID doctorId, CreateDoctorScheduleRequest req) {
        Doctor doctor = doctorRepo.findById(doctorId).
                orElseThrow(()-> new ResourceNotFoundException("Doctor with id "+doctorId+" not found"));
        if (doctorScheduleRepo.existsByDoctorIdAndDayOfWeek(doctorId, req.dayOfWeek())){
            throw new ConflictException("Doctor with id "+doctorId+" already has a schedule for "+req.dayOfWeek());
        }
        DoctorSchedule doctorSchedule = DoctorSchedule.create(doctor,req.dayOfWeek(),req.startTime(),req.endTime());
        return doctorScheduleMapper.toResponse(doctorScheduleRepo.save(doctorSchedule));
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorScheduleResponse findByDoctorIdAndDayOfWeek(UUID doctorId, DayOfWeek dayOfWeek) {
        return doctorScheduleRepo.findByDoctorIdAndDayOfWeek(doctorId,dayOfWeek).map(doctorScheduleMapper::toResponse).
                orElseThrow(()-> new ResourceNotFoundException("Doctor schedule with doctor id "+doctorId+
                                                            " and day of week "+dayOfWeek+" not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorScheduleResponse> findAll(UUID doctorId) {
        return doctorScheduleRepo.findByDoctorId(doctorId).stream().map(doctorScheduleMapper::toResponse).toList();
    }
}
