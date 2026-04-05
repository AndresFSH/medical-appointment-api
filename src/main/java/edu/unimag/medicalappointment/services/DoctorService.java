package edu.unimag.medicalappointment.services;

import edu.unimag.medicalappointment.dto.DoctorDTO.*;

import java.util.List;
import java.util.UUID;

public interface DoctorService {

    DoctorResponse createDoctor(CreateDoctorRequest req);
    DoctorResponse updateDoctor(UUID id, UpdateDoctorRequest req);
    DoctorResponse findById(UUID id);
    List<DoctorResponse> findAll();

}
