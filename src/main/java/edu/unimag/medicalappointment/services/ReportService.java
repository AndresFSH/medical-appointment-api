package edu.unimag.medicalappointment.services;

import edu.unimag.medicalappointment.dto.ReportDTO.*;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportService {

    List<OfficeOccupancyResponse> getOfficeOccupancy(LocalDateTime from, LocalDateTime to);
    List<DoctorProductivityResponse> getDoctorProductivity();
    List<NoShowPatientResponse> getNoShowPatient(LocalDateTime from, LocalDateTime to);

}
