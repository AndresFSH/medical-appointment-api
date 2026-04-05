package edu.unimag.medicalappointment.services;

import edu.unimag.medicalappointment.domain.entity.Doctor;
import edu.unimag.medicalappointment.domain.entity.Specialty;
import edu.unimag.medicalappointment.dto.DoctorDTO.*;
import edu.unimag.medicalappointment.exception.ResourceNotFoundException;
import edu.unimag.medicalappointment.mapper.DoctorMapper;
import edu.unimag.medicalappointment.repository.DoctorRepository;
import edu.unimag.medicalappointment.repository.SpecialtyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepo;
    private final SpecialtyRepository specialtyRepo;
    private final DoctorMapper doctorMapper;

    @Override
    @Transactional
    public DoctorResponse createDoctor(CreateDoctorRequest req) {
        Specialty specialty = specialtyRepo.findById(req.specialtyId()).
                orElseThrow(()-> new ResourceNotFoundException("Specialty with id "+req.specialtyId()+" not found"));

        Doctor doctor = Doctor.builder().fullName(req.fullName()).specialty(specialty).build();
        return doctorMapper.toResponse(doctorRepo.save(doctor));
    }

    @Override
    @Transactional
    public DoctorResponse updateDoctor(UUID id, UpdateDoctorRequest req) {
        Doctor doctor = doctorRepo.findById(id).
                orElseThrow(()-> new ResourceNotFoundException("Doctor with id "+id+" not found"));

        Specialty specialty = specialtyRepo.findById(req.specialtyId()).
                orElseThrow(()-> new ResourceNotFoundException("Specialty with id "+req.specialtyId()+" not found"));

        doctor.setFullName(req.fullName());
        doctor.setSpecialty(specialty);

        if(req.active()){
            doctor.activate();
        }else {
            doctor.deactivate();
        }
        return doctorMapper.toResponse(doctorRepo.save(doctor));
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorResponse findById(UUID id) {
        return doctorRepo.findById(id).map(doctorMapper::toResponse).
                orElseThrow(()-> new ResourceNotFoundException("doctor with id "+id+" not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorResponse> findAll() {
        return doctorRepo.findAll().stream().map(doctorMapper::toResponse).toList();
    }
}
