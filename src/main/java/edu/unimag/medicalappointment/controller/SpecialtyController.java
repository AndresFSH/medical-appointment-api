package edu.unimag.medicalappointment.controller;

import edu.unimag.medicalappointment.services.SpecialtyService;
import edu.unimag.medicalappointment.dto.SpecialtyDTO.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/specialties")
@RequiredArgsConstructor
@Validated
public class SpecialtyController {

    private final SpecialtyService specialtyService;

    @PostMapping
    public ResponseEntity<SpecialtyResponse> createSpecialty(@Valid @RequestBody CreateSpecialtyRequest req,
                                                              UriComponentsBuilder uriBuilder){
        var specialtyCreated = specialtyService.createSpecialty(req);
        var location = uriBuilder.path("/api/specialties/{id}").buildAndExpand(specialtyCreated.specialtyId()).toUri();
        return ResponseEntity.created(location).body(specialtyCreated);
    }

    @GetMapping
    public ResponseEntity<List<SpecialtyResponse>> findAll(){
        return ResponseEntity.ok(specialtyService.findAll());
    }

}
