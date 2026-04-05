package edu.unimag.medicalappointment.services;

import edu.unimag.medicalappointment.dto.AppointmentDTO.*;

import java.util.List;
import java.util.UUID;

public interface AppointmentService {

    AppointmentResponse createAppointment(CreateAppointmentRequest req);
    AppointmentResponse findById(UUID id);
    List<AppointmentResponse> findAll();
    AppointmentResponse confirmAppointment(UUID id);
    AppointmentResponse cancelAppointment(UUID id, CancelAppointmentRequest req);
    AppointmentResponse completeAppointment(UUID id, CompleteAppointmentRequest req);
    AppointmentResponse setAsNoShowAppointment(UUID id);

}
