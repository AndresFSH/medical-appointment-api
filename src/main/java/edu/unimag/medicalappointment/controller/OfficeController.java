package edu.unimag.medicalappointment.controller;

import edu.unimag.medicalappointment.dto.OfficeDTO.*;
import edu.unimag.medicalappointment.services.OfficeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/offices")
@RequiredArgsConstructor
@Validated
public class OfficeController {

    private final OfficeService officeService;

    @PostMapping
    public ResponseEntity<OfficeResponse> createOffice(@Valid @RequestBody CreateOfficeRequest req,
                                                       UriComponentsBuilder uriBuilder) {
        var officeCreated = officeService.createOffice(req);
        var location =  uriBuilder.path("/api/offices/{id}").buildAndExpand(officeCreated.officeId()).toUri();
        return ResponseEntity.created(location).body(officeCreated);
    }

    @GetMapping
    public ResponseEntity<List<OfficeResponse>> findAll(){
        return ResponseEntity.ok(officeService.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<OfficeResponse> updateOffice(@Valid @RequestBody UpdateOfficeRequest req,
                                                       @PathVariable UUID id){
        return ResponseEntity.ok(officeService.updateOffice(id, req));
    }

}
