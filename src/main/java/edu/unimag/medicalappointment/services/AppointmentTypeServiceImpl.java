package edu.unimag.medicalappointment.services;

import edu.unimag.medicalappointment.domain.entity.AppointmentType;
import edu.unimag.medicalappointment.dto.AppointmentTypeDTO.*;
import edu.unimag.medicalappointment.exception.ConflictException;
import edu.unimag.medicalappointment.mapper.AppointmentTypeMapper;
import edu.unimag.medicalappointment.repository.AppointmentTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentTypeServiceImpl implements AppointmentTypeService {

    private final AppointmentTypeRepository appointmentTypeRepo;
    private final AppointmentTypeMapper appointmentTypeMapper;

    @Override
    @Transactional
    public AppointmentTypeResponse createAppointmentType(CreateAppointmentTypeRequest req) {
        if(appointmentTypeRepo.existsByName(req.name())){
            throw new ConflictException("Appointment type with name " + req.name() + " already exists");
        }
        AppointmentType appointmentType = appointmentTypeMapper.toEntity(req);
        return  appointmentTypeMapper.toResponse(appointmentTypeRepo.save(appointmentType));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentTypeResponse> findAll() {
        return appointmentTypeRepo.findAll().stream().map(appointmentTypeMapper::toResponse).toList();
    }
}
