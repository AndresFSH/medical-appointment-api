package edu.unimag.medicalappointment.services;

import edu.unimag.medicalappointment.dto.PatientDTO.*;

import java.util.List;
import java.util.UUID;

public interface PatientService {

    PatientResponse createPatient(CreatePatientRequest req);
    PatientResponse updatePatient(UUID id,UpdatePatientRequest req);
    PatientResponse findById(UUID id);
    List<PatientResponse> findAll();

}
