package edu.unimag.medicalappointment.services;

import edu.unimag.medicalappointment.domain.entity.Patient;
import edu.unimag.medicalappointment.domain.entity.enums.PatientStatus;
import edu.unimag.medicalappointment.dto.PatientDTO.*;
import edu.unimag.medicalappointment.exception.ConflictException;
import edu.unimag.medicalappointment.exception.ResourceNotFoundException;
import edu.unimag.medicalappointment.mapper.PatientMapper;
import edu.unimag.medicalappointment.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService{

    private final PatientRepository patientRepo;
    private final PatientMapper patientMapper;

    @Override
    @Transactional
    public PatientResponse createPatient(CreatePatientRequest req) {
        if(patientRepo.existsByEmail(req.email())) {
            throw new ConflictException("A patient with email "+req.email()+"   already exists");
        }
        Patient patient = patientMapper.toEntity(req);
        return patientMapper.toResponse(patientRepo.save(patient));
    }

    @Override
    @Transactional
    public PatientResponse updatePatient(UUID id, UpdatePatientRequest req) {
        Patient patient = patientRepo.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("Patient with id "+id+" not found"));
        if(!patient.getEmail().equals(req.email()) && patientRepo.existsByEmail(req.email())) {
            throw new ConflictException("A patient with email "+req.email()+" already exists");
        }
        patient.setFullName(req.fullName());
        patient.setEmail(req.email());
        if (req.status() == PatientStatus.ACTIVE) {
            patient.activate();
        } else {
            patient.deactivate();
        }

        return patientMapper.toResponse(patientRepo.save(patient));
    }

    @Override
    @Transactional(readOnly = true)
    public PatientResponse findById(UUID id) {
        return patientRepo.findById(id).map(patientMapper::toResponse).
                orElseThrow(()-> new ResourceNotFoundException("Patient with id "+id+" not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientResponse> findAll() {
        return patientRepo.findAll().stream().map(patientMapper::toResponse).toList();
    }
}
