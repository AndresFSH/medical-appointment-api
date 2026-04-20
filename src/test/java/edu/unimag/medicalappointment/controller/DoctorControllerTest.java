package edu.unimag.medicalappointment.controller;

import edu.unimag.medicalappointment.dto.SpecialtyDTO.*;
import edu.unimag.medicalappointment.exception.ResourceNotFoundException;
import edu.unimag.medicalappointment.services.DoctorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import edu.unimag.medicalappointment.dto.DoctorDTO.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DoctorController.class)
public class DoctorControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;
    @MockitoBean DoctorService doctorService;

    private static UUID id;
    private static SpecialtySummary specialty;

    @BeforeEach
    public void setup() {
        id = UUID.randomUUID();
        specialty = new SpecialtySummary(UUID.randomUUID(), "Specialty 1");
    }

    @Test
    void createDoctor_returns201AndLocation() throws Exception{
        var req = new CreateDoctorRequest("Doctor", specialty.specialtyId());
        var res = new DoctorResponse(id, "Doctor", true, specialty);

        when(doctorService.createDoctor(any())).thenReturn(res);

        mvc.perform(post("/api/doctors").contentType(MediaType.APPLICATION_JSON).
                content(om.writeValueAsString(req))).andExpect(status().isCreated()).
                andExpect(header().exists("Location")).andExpect(jsonPath("$.doctorId").
                        value(id.toString())).andExpect(jsonPath("$.fullName").value("Doctor")).
                andExpect(jsonPath("$.specialty.specialtyId").value(specialty.specialtyId().toString())).
                andExpect(jsonPath("$.specialty.name").value("Specialty 1"));
    }

    @Test
    void createDoctor_whenInvalidRequest_returns400() throws Exception{
        var req = new CreateDoctorRequest("", null);

        mvc.perform(post("/api/doctors").contentType(MediaType.APPLICATION_JSON).
                content(om.writeValueAsString(req))).andExpect(status().isBadRequest());
    }

    @Test
    void updateDoctor_returns200() throws Exception{
        var req = new UpdateDoctorRequest("newDoctor",false,specialty.specialtyId());
        var res = new DoctorResponse(id, "Doctor", true, specialty);

        when(doctorService.updateDoctor(eq(id), any())).thenReturn(res);

        mvc.perform(put("/api/doctors/{id}",id).contentType(MediaType.APPLICATION_JSON).
                content(om.writeValueAsString(req))).andExpect(status().isOk());
    }

    @Test
    void findById_whenExists_returns200() throws Exception{
        var res = new DoctorResponse(id, "Doctor", true, specialty);

        when(doctorService.findById(id)).thenReturn(res);

        mvc.perform(get("/api/doctors/{id}",id)).andExpect(status().isOk()).
                andExpect(jsonPath("$.doctorId").value(id.toString()));
    }

    @Test
    void findById_whenNotExists_returns404() throws Exception{
        when(doctorService.findById(id)).thenThrow(new ResourceNotFoundException("Doctor with id "+id+" not found"));
        mvc.perform(get("/api/doctors/{id}",id)).andExpect(status().isNotFound()).
                andExpect(jsonPath("$.message").value(containsString("not found")));
    }

    @Test
    void findAll_returns200() throws Exception{
        var res = List.of(new DoctorResponse(id, "Doctor", true, specialty));

        when(doctorService.findAll()).thenReturn(res);

        mvc.perform(get("/api/doctors")).andExpect(status().isOk()).
                andExpect(jsonPath("$[0].fullName").value("Doctor"));
    }

}
