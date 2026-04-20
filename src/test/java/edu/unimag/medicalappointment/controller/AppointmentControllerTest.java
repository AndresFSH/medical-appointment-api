package edu.unimag.medicalappointment.controller;

import edu.unimag.medicalappointment.domain.entity.enums.AppointmentStatus;
import edu.unimag.medicalappointment.dto.AppointmentDTO.*;
import edu.unimag.medicalappointment.dto.DoctorDTO.DoctorSummary;
import edu.unimag.medicalappointment.dto.PatientDTO.PatientSummary;
import edu.unimag.medicalappointment.dto.OfficeDTO.OfficeSummary;
import edu.unimag.medicalappointment.dto.AppointmentTypeDTO.AppointmentTypeSummary;
import edu.unimag.medicalappointment.dto.SpecialtyDTO.SpecialtySummary;
import edu.unimag.medicalappointment.exception.BusinessException;
import edu.unimag.medicalappointment.exception.ConflictException;
import edu.unimag.medicalappointment.exception.ResourceNotFoundException;
import edu.unimag.medicalappointment.services.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(AppointmentController.class)
public class AppointmentControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;
    @MockitoBean AppointmentService service;

    private static final LocalDateTime BASE = LocalDateTime.of(2026,6,19,12,30);

    private UUID patientId;
    private UUID doctorId;
    private UUID officeId;
    private UUID typeId;
    private UUID appointmentId;

    private static PatientSummary patient;
    private static DoctorSummary doctor;
    private static OfficeSummary office;
    private static AppointmentTypeSummary type;

    @BeforeEach
    void setup() {
        patientId = UUID.randomUUID();
        doctorId = UUID.randomUUID();
        officeId = UUID.randomUUID();
        typeId = UUID.randomUUID();
        appointmentId = UUID.randomUUID();

        patient = new PatientSummary(patientId,"Patient");
        SpecialtySummary specialty = new SpecialtySummary(UUID.randomUUID(), "Specialty");
        doctor = new DoctorSummary(doctorId, "Doctor", specialty);
        office = new OfficeSummary(officeId, "Office", "Location");
        type = new AppointmentTypeSummary(typeId,"Type");

    }

    @Test
    void createAppointment_returns201AndLocation() throws Exception {
        var req = new CreateAppointmentRequest(patientId, doctorId, officeId, typeId, BASE);
        var res = new AppointmentResponse(appointmentId, patient, doctor, office,type, BASE, BASE.plusHours(1),
                AppointmentStatus.SCHEDULED, null, null);

        when(service.createAppointment(any())).thenReturn(res);

        mvc.perform(post("/api/appointments").contentType(MediaType.APPLICATION_JSON).
                content(om.writeValueAsString(req))).andExpect(status().isCreated()).
                andExpect(header().exists("Location")).andExpect(jsonPath("$.appointmentId").
                        value(appointmentId.toString())).andExpect(jsonPath("$.patient.patientId").
                        value(patientId.toString())).andExpect(jsonPath("$.doctor.doctorId").
                        value(doctorId.toString())).andExpect(jsonPath("$.office.officeId").
                        value(officeId.toString()));
    }

    @Test
    void createAppointment_whenScheduleConflict_returns409() throws Exception {
        var req = new CreateAppointmentRequest(patientId, doctorId, officeId, typeId, BASE);

        when(service.createAppointment(any())).thenThrow(
                new ConflictException("Exists a schedule conflict in that time range"));
        mvc.perform(post("/api/appointments").contentType(MediaType.APPLICATION_JSON).
                        content(om.writeValueAsString(req))).andExpect(status().isConflict()).
                andExpect(jsonPath("$.message").value(containsString("conflict")));
    }

    @Test
    void createAppointment_whenInvalidRequest_returns400() throws Exception {
        var req = new CreateAppointmentRequest(null,null,null,null,
                LocalDateTime.of(2000,1,12,12,0));

        mvc.perform(post("/api/appointments").contentType(MediaType.APPLICATION_JSON).
                content(om.writeValueAsString(req))).andExpect(status().isBadRequest());
    }

    @Test
    void createAppointment_whenPatientNotFound_returns404() throws Exception {
        var req = new CreateAppointmentRequest(patientId,doctorId,officeId,typeId,BASE);

        when(service.createAppointment(any())).thenThrow(new ResourceNotFoundException("Patient with id "+patientId+
                " not found"));

        mvc.perform(post("/api/appointments").contentType(MediaType.APPLICATION_JSON).
                content(om.writeValueAsString(req))).andExpect(status().isNotFound()).
                andExpect(jsonPath("$.message").value(containsString("not found")));
    }

    @Test
    void createAppointment_whenDoctorInactive_returns422() throws Exception {
        var req = new CreateAppointmentRequest(patientId,doctorId,officeId,typeId,BASE);

        when(service.createAppointment(any())).thenThrow(
                new BusinessException("Doctor with id "+doctorId+" is inactive"));

        mvc.perform(post("/api/appointments").contentType(MediaType.APPLICATION_JSON).
                content(om.writeValueAsString(req))).andExpect(status().isUnprocessableEntity()).
                andExpect(jsonPath("$.message").value(containsString("inactive")));
    }

    @Test
    void findById_whenExists_returns200() throws Exception {
        var res = new AppointmentResponse(appointmentId,patient,doctor,office,type,BASE, BASE.plusHours(1),
                AppointmentStatus.SCHEDULED, null, null);

        when(service.findById(appointmentId)).thenReturn(res);

        mvc.perform(get("/api/appointments/{id}",appointmentId)).andExpect(status().isOk()).
                andExpect(jsonPath("$.appointmentId").value(appointmentId.toString()));
    }

    @Test
    void findById_whenNotFound_returns404() throws Exception {
        when(service.findById(appointmentId)).thenThrow(
                new ResourceNotFoundException("Appointment with id "+appointmentId+" not found"));

        mvc.perform(get("/api/appointments/{id}",appointmentId)).andExpect(status().isNotFound()).
                andExpect(jsonPath("$.message").value(containsString("not found")));
    }

    @Test
    void findAll_returns200() throws Exception {
        var res = List.of(new AppointmentResponse(appointmentId,patient,doctor,office,type, BASE, BASE.plusHours(1),
                AppointmentStatus.SCHEDULED, null, null));

        when(service.findAll()).thenReturn(res);

        mvc.perform(get("/api/appointments")).andExpect(status().isOk()).
                andExpect(jsonPath("$[0].appointmentId").value(appointmentId.toString()));
    }

    @Test
    void confirmAppointment_returns200() throws Exception {
        var res = new AppointmentResponse(appointmentId, patient, doctor, office, type, BASE, BASE.plusHours(1),
                AppointmentStatus.CONFIRMED, null, null);

        when(service.confirmAppointment(any())).thenReturn(res);

        mvc.perform(put("/api/appointments/{id}/confirm",appointmentId)).andExpect(status().isOk()).
                andExpect(jsonPath("$.status").value(AppointmentStatus.CONFIRMED.toString()));
    }

    @Test
    void cancelAppointment_returns200() throws Exception {
        var req = new CancelAppointmentRequest("reason");
        var res = new AppointmentResponse(appointmentId,patient,doctor,office,type,BASE, BASE.plusHours(1),
                AppointmentStatus.CANCELLED, "reason", null);

        when(service.cancelAppointment(eq(appointmentId), any())).thenReturn(res);

        mvc.perform(put("/api/appointments/{id}/cancel",appointmentId).
                        contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(req)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").
                        value(AppointmentStatus.CANCELLED.toString())).andExpect(jsonPath("$.cancellationReason").
                        value("reason"));
    }

    @Test
    void completeAppointment_returns200() throws Exception {
        var req = new CompleteAppointmentRequest("observations");
        var res = new AppointmentResponse(appointmentId, patient, doctor, office, type, BASE, BASE.plusHours(1),
                AppointmentStatus.COMPLETED, null, null);

        when(service.completeAppointment(eq(appointmentId), any())).thenReturn(res);

        mvc.perform(put("/api/appointments/{id}/complete",appointmentId).
                contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(req)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").
                        value(AppointmentStatus.COMPLETED.toString()));
    }

    @Test
    void completeAppointment_whenInFuture_returns422() throws Exception {
        var req = new CompleteAppointmentRequest("observations");

        when(service.completeAppointment(eq(appointmentId), any())).thenThrow(
                new BusinessException("Appointment can't be completed before its start time"));

        mvc.perform(put("/api/appointments/{id}/complete",appointmentId).
                contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(req))).
                andExpect(status().isUnprocessableEntity()).andExpect(jsonPath("$.message").
                value(containsString("before its start time")));
    }

    @Test
    void setAsNoShow_returns200() throws Exception {
        var res = new AppointmentResponse(appointmentId, patient, doctor, office, type, BASE, BASE.plusHours(1),
                AppointmentStatus.NO_SHOW, null, null);

        when(service.setAsNoShowAppointment(any())).thenReturn(res);

        mvc.perform(put("/api/appointments/{id}/no-show", appointmentId)).andExpect(status().isOk()).
                andExpect(jsonPath("$.status").value(AppointmentStatus.NO_SHOW.toString()));
    }

}
