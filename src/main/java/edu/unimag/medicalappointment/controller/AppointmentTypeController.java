package edu.unimag.medicalappointment.controller;

import edu.unimag.medicalappointment.dto.AppointmentTypeDTO.*;
import edu.unimag.medicalappointment.services.AppointmentTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/appointment-types")
@RequiredArgsConstructor
@Validated
public class AppointmentTypeController {

    private final AppointmentTypeService appointmentTypeService;

    @PostMapping
    public ResponseEntity<AppointmentTypeResponse> createAppointmentType(@Valid @RequestBody CreateAppointmentTypeRequest req,
                                                                         UriComponentsBuilder uriBuilder){
        var typeCreated = appointmentTypeService.createAppointmentType(req);
        var location = uriBuilder.path("/api/appointment-types/{id}").buildAndExpand(typeCreated.id()).toUri();
        return ResponseEntity.created(location).body(typeCreated);
    }

    @GetMapping
    public ResponseEntity<List<AppointmentTypeResponse>> getAll(){
        return ResponseEntity.ok(appointmentTypeService.findAll());
    }

}
