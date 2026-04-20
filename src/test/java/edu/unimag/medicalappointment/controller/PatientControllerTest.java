package edu.unimag.medicalappointment.controller;

import edu.unimag.medicalappointment.domain.entity.enums.PatientStatus;
import edu.unimag.medicalappointment.exception.GlobalExceptionHandler;
import edu.unimag.medicalappointment.exception.ResourceNotFoundException;
import edu.unimag.medicalappointment.services.PatientService;
import edu.unimag.medicalappointment.dto.PatientDTO.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;

@WebMvcTest(PatientController.class)
public class PatientControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;
    @MockitoBean PatientService patientService;

    private static UUID id;

    @BeforeEach
    void setUp(){
        id = UUID.randomUUID();
    }

    @Test
    void createPatient_returns201AndLocation() throws Exception {
        var req = new CreatePatientRequest("Andres", "andres@mail.com");
        var res = new PatientResponse(id,"Andres", "andres@mail.com",
                PatientStatus.ACTIVE);

        when(patientService.createPatient(any())).thenReturn(res);

        mvc.perform(post("/api/patients").contentType(MediaType.APPLICATION_JSON).
                content(om.writeValueAsString(req))).andExpect(status().isCreated()).
                andExpect(header().exists("Location")).andExpect(jsonPath("$.patientId").
                        value(id.toString())).andExpect(jsonPath("$.fullName").value("Andres")).
                andExpect(jsonPath("$.email").value("andres@mail.com"));
    }

    @Test
    void createPatient_whenInvalidRequest_returns400() throws Exception {
        var req = new CreatePatientRequest("","notAnEmail");

        mvc.perform(post("/api/patients").contentType(MediaType.APPLICATION_JSON).
                content(om.writeValueAsString(req))).andExpect(status().isBadRequest());
    }

    @Test
    void updatePatient_returns200() throws Exception{
        var req = new UpdatePatientRequest("Juan", "juan@mail.com", PatientStatus.INACTIVE);
        var res = new PatientResponse(id, "Juan", "new@mail.com",  PatientStatus.INACTIVE);

        when(patientService.updatePatient(eq(id),any())).thenReturn(res);

        mvc.perform(put("/api/patients/{id}", id).contentType(MediaType.APPLICATION_JSON).
                content(om.writeValueAsString(req))).andExpect(status().isOk());
    }

    @Test
    void findById_whenExists_returns200() throws Exception {
        var res = new PatientResponse(id, "Juan", "juan@mail.com",  PatientStatus.ACTIVE);

        when((patientService.findById(id))).thenReturn(res);

        mvc.perform(get("/api/patients/{id}", id)).andExpect(status().isOk()).
                andExpect(jsonPath("$.patientId").value(id.toString()));
    }

    @Test
    void findById_whenNotExists_returns404() throws Exception {
        when(patientService.findById(id)).thenThrow(new ResourceNotFoundException("Patient with id "+id+" not found"));
        mvc.perform(get("/api/patients/{id}", id)).andExpect(status().isNotFound()).
                andExpect(jsonPath("$.message").value(containsString("not found")));
    }

    @Test
    void findAll_returns200() throws Exception {
        var res = List.of(new PatientResponse(id, "Andres", "andres@mail.com", PatientStatus.ACTIVE));

        when(patientService.findAll()).thenReturn(res);

        mvc.perform(get("/api/patients")).andExpect(status().isOk()).
                andExpect(jsonPath("$[0].fullName").value("Andres"));
    }

}
