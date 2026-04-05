package edu.unimag.medicalappointment.services;

import edu.unimag.medicalappointment.domain.entity.enums.AppointmentStatus;
import edu.unimag.medicalappointment.exception.ResourceNotFoundException;
import edu.unimag.medicalappointment.repository.AppointmentRepository;
import edu.unimag.medicalappointment.repository.DoctorRepository;
import edu.unimag.medicalappointment.repository.OfficeRepository;
import edu.unimag.medicalappointment.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import edu.unimag.medicalappointment.dto.ReportDTO.*;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final AppointmentRepository appointmentRepo;

    @Override
    @Transactional(readOnly = true)
    public List<OfficeOccupancyResponse> getOfficeOccupancy(LocalDateTime from, LocalDateTime to) {
        return appointmentRepo.calculateOfficeOccupancy(from, to).stream().map(p-> new OfficeOccupancyResponse(
                p.officeId(), p.officeName(), p.appointmentCount())).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorProductivityResponse> getDoctorProductivity() {
        return appointmentRepo.countCompletedAppointmentsByDoctor(AppointmentStatus.COMPLETED).stream().map(p->
                new DoctorProductivityResponse(p.doctorId(), p.doctorName(), p.appointmentCount())).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoShowPatientResponse> getNoShowPatient(LocalDateTime from, LocalDateTime to) {
        return appointmentRepo.countNoShowAppointmentsByPatient(AppointmentStatus.NO_SHOW, from, to).stream()
                .map(p-> new NoShowPatientResponse(p.patientId(), p.patientName(), p.appointmentCount())).toList();
    }
}
