package edu.unimag.medicalappointment.services;

import edu.unimag.medicalappointment.dto.SpecialtyDTO.*;

import java.util.List;

public interface SpecialtyService {

    SpecialtyResponse createSpecialty(CreateSpecialtyRequest req);
    List<SpecialtyResponse> findAll();

}
