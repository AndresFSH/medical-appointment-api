package edu.unimag.medicalappointment.services;

import edu.unimag.medicalappointment.dto.AppointmentTypeDTO.*;

import java.util.List;

public interface AppointmentTypeService {

    AppointmentTypeResponse createAppointmentType(CreateAppointmentTypeRequest req);
    List<AppointmentTypeResponse> findAll();

}
