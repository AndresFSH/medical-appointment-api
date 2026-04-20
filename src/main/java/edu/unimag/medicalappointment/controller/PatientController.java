package edu.unimag.medicalappointment.controller;

import edu.unimag.medicalappointment.services.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import edu.unimag.medicalappointment.dto.PatientDTO.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Validated
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    public ResponseEntity<PatientResponse> createPatient(@Valid @RequestBody CreatePatientRequest req,
                                                         UriComponentsBuilder uriBuilder) {
        var patientCreated = patientService.createPatient(req);
        var location = uriBuilder.path("/api/patients/{id}").buildAndExpand(patientCreated.patientId()).toUri();
        return ResponseEntity.created(location).body(patientCreated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(patientService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<PatientResponse>> findAll(){
        return ResponseEntity.ok(patientService.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientResponse> updatePatient(@Valid @RequestBody UpdatePatientRequest req,
                                                         @PathVariable UUID id){
        return ResponseEntity.ok(patientService.updatePatient(id,req));
    }

}
