package edu.unimag.medicalappointment.services;

import edu.unimag.medicalappointment.domain.entity.*;
import edu.unimag.medicalappointment.domain.entity.enums.AppointmentStatus;
import edu.unimag.medicalappointment.domain.entity.enums.OfficeStatus;
import edu.unimag.medicalappointment.domain.entity.enums.PatientStatus;
import edu.unimag.medicalappointment.dto.AppointmentDTO.*;
import edu.unimag.medicalappointment.exception.BusinessException;
import edu.unimag.medicalappointment.exception.ConflictException;
import edu.unimag.medicalappointment.exception.ResourceNotFoundException;
import edu.unimag.medicalappointment.mapper.AppointmentMapper;
import edu.unimag.medicalappointment.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepo;
    private final PatientRepository patientRepo;
    private final DoctorRepository doctorRepo;
    private final OfficeRepository officeRepo;
    private final AppointmentTypeRepository appointmentTypeRepo;
    private final DoctorScheduleService doctorScheduleService;
    private final AppointmentMapper  appointmentMapper;

    private static final List<AppointmentStatus> ACTIVE_STATUSES=
            List.of(AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED);

    @Override
    @Transactional
    public AppointmentResponse createAppointment(CreateAppointmentRequest req) {
        Patient patient = patientRepo.findById(req.patientId()).
                orElseThrow(()->new ResourceNotFoundException("Patient with id: "+req.patientId()+" not found"));
        if(patient.getStatus() != PatientStatus.ACTIVE){
            throw new BusinessException("Patient with id "+req.patientId()+" is not active.");
        }

        Doctor doctor = doctorRepo.findById(req.doctorId()).
                orElseThrow(()->new ResourceNotFoundException("Doctor id: "+req.doctorId()+" not found"));
        if(!doctor.isActive()){
            throw new BusinessException("Doctor with id "+req.doctorId()+" is not active.");
        }

        Office office = officeRepo.findById(req.officeId()).
                orElseThrow(()->new ResourceNotFoundException("Office id: "+req.officeId()+" not found"));
        if (office.getStatus() != OfficeStatus.AVAILABLE) {
            throw new BusinessException("Office with id "+req.officeId()+" is not available.");
        }

        AppointmentType appointmentType = appointmentTypeRepo.findById(req.typeId()).
                orElseThrow(()->new ResourceNotFoundException("Appointment type id: "+req.typeId()+" not found"));

        if (!req.startAt().isAfter(LocalDateTime.now())){
            throw new BusinessException("Appointment date must be in the future.");
        }

        LocalDateTime endAt = req.startAt().plusMinutes(appointmentType.getDurationMinutes());
        var schedule = doctorScheduleService.findByDoctorIdAndDayOfWeek(doctor.getId(), req.startAt().getDayOfWeek());
        if (req.startAt().toLocalTime().isBefore(schedule.startTime()) ||
                endAt.toLocalTime().isAfter(schedule.endTime())){
            throw new BusinessException("Appointment is outside the doctor's working hours.");
        }

        if (appointmentRepo.existsOverlappingDoctor(req.doctorId(),req.startAt(), endAt, ACTIVE_STATUSES)){
            throw new ConflictException("Doctor already has an appointment in that time range.");
        }

        if(appointmentRepo.existsOverlappingOffice(req.officeId(),req.startAt(), endAt, ACTIVE_STATUSES)){
            throw new ConflictException("Office already has an appointment in that time range.");
        }

        if(appointmentRepo.existsOverlappingPatient(req.patientId(),req.startAt(), endAt, ACTIVE_STATUSES)){
            throw new ConflictException("Patient already has an appointment in that time range.");
        }

        Appointment appointment = Appointment.schedule(patient,doctor,office,appointmentType,req.startAt());
        return appointmentMapper.toResponse(appointmentRepo.save(appointment));

    }

    @Override
    @Transactional(readOnly = true)
    public AppointmentResponse findById(UUID id) {
        return appointmentRepo.findById(id).map(appointmentMapper::toResponse).
                orElseThrow(()->new ResourceNotFoundException("Appointment with id "+id+" not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> findAll() {
        return appointmentRepo.findAll().stream().map(appointmentMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public AppointmentResponse confirmAppointment(UUID id) {
        Appointment appointment = appointmentRepo.findById(id).
                orElseThrow(()->new ResourceNotFoundException("Appointment with id "+id+" not found"));

        appointment.confirm();
        return appointmentMapper.toResponse(appointmentRepo.save(appointment));
    }

    @Override
    @Transactional
    public AppointmentResponse cancelAppointment(UUID id, CancelAppointmentRequest req) {
        Appointment appointment = appointmentRepo.findById(id).
                orElseThrow(()->new ResourceNotFoundException("Appointment with id "+id+" not found"));

        appointment.cancel(req.cancellationReason());
        return appointmentMapper.toResponse(appointmentRepo.save(appointment));
    }

    @Override
    @Transactional
    public AppointmentResponse completeAppointment(UUID id, CompleteAppointmentRequest req) {
        Appointment appointment = appointmentRepo.findById(id).
                orElseThrow(()->new ResourceNotFoundException("Appointment with id "+id+" not found"));

        if(LocalDateTime.now().isBefore(appointment.getStartAt())){
            throw new BusinessException("Appointment can't be completed before its start time.");
        }

        appointment.complete(req.observations());
        return appointmentMapper.toResponse(appointmentRepo.save(appointment));
    }

    @Override
    @Transactional
    public AppointmentResponse setAsNoShowAppointment(UUID id) {
        Appointment appointment = appointmentRepo.findById(id).
                orElseThrow(()->new ResourceNotFoundException("Appointment with id "+id+" not found"));

        if(LocalDateTime.now().isBefore(appointment.getStartAt())){
            throw new BusinessException("Appointment cannot be mark as no-show before its start time.");
        }
        appointment.markNoShow();
        return appointmentMapper.toResponse(appointmentRepo.save(appointment));
    }
}
