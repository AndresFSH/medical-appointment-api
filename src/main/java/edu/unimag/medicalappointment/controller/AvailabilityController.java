package edu.unimag.medicalappointment.controller;

import edu.unimag.medicalappointment.dto.AvailabilityDTO.*;
import edu.unimag.medicalappointment.services.AvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/availability/doctors")
@RequiredArgsConstructor
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @GetMapping("/{doctorId}")
    public ResponseEntity<List<AvailabilitySlotResponse>> getAvailabilitySlot(@PathVariable UUID doctorId,
                                                                             @RequestParam LocalDate date,
                                                                             @RequestParam UUID appointmentTypeId) {
        var slot = availabilityService.getAvailabilitySlot(doctorId, date, appointmentTypeId);
        return ResponseEntity.ok(slot);
    }

}
