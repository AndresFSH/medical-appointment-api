package edu.unimag.medicalappointment.services;

import edu.unimag.medicalappointment.dto.OfficeDTO.*;

import java.util.List;
import java.util.UUID;

public interface OfficeService {

    OfficeResponse createOffice(CreateOfficeRequest req);
    OfficeResponse updateOffice(UUID id, UpdateOfficeRequest req);
    List<OfficeResponse> findAll();

}
