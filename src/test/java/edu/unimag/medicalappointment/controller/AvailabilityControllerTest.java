package edu.unimag.medicalappointment.controller;

import edu.unimag.medicalappointment.domain.entity.AppointmentType;
import edu.unimag.medicalappointment.dto.AvailabilityDTO.AvailabilitySlotResponse;
import edu.unimag.medicalappointment.services.AvailabilityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;

@WebMvcTest(AvailabilityController.class)
public class AvailabilityControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;
    @MockitoBean AvailabilityService service;

    private static final LocalDateTime BASE = LocalDateTime.now();

    @Test
    public void getAvailabilitySlot() throws Exception {
        UUID doctorId = UUID.randomUUID();
        AppointmentType type = AppointmentType.builder().id(UUID.randomUUID()).name("type").durationMinutes(60).build();
        var res = List.of(new AvailabilitySlotResponse(BASE.plusHours(1), BASE.plusHours(2)));

        when(service.getAvailabilitySlot(doctorId,BASE.toLocalDate(),type.getId())).thenReturn(res);

        mvc.perform(get("/api/availability/doctors/{doctorId}",doctorId).
                param("date",BASE.toLocalDate().toString()).
                param("appointmentTypeId",type.getId().toString())).andExpect(status().isOk());
    }

}
