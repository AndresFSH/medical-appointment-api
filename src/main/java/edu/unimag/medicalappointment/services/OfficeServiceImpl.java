package edu.unimag.medicalappointment.services;

import edu.unimag.medicalappointment.dto.OfficeDTO.*;
import edu.unimag.medicalappointment.domain.entity.Office;
import edu.unimag.medicalappointment.exception.ConflictException;
import edu.unimag.medicalappointment.exception.ResourceNotFoundException;
import edu.unimag.medicalappointment.mapper.OfficeMapper;
import edu.unimag.medicalappointment.repository.OfficeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OfficeServiceImpl implements OfficeService {

    private final OfficeRepository officeRepo;
    private final OfficeMapper officeMapper;

    @Override
    @Transactional
    public OfficeResponse createOffice(CreateOfficeRequest req) {
        if(officeRepo.existsByName(req.name())){
            throw new ConflictException("Office with name " + req.name() + " already exists");
        }
        Office office = officeMapper.toEntity(req);
        return officeMapper.toResponse(officeRepo.save(office));
    }

    @Override
    @Transactional
    public OfficeResponse updateOffice(UUID id, UpdateOfficeRequest req) {
        Office office = officeRepo.findById(id).
                orElseThrow(()-> new ResourceNotFoundException("Office with id " + id + " not found"));
        if(!office.getName().equals(req.name()) && officeRepo.existsByName(req.name())){
            throw new ConflictException("Office with name " + req.name() + " already exists");
        }
        office.setName(req.name());
        office.setLocation(req.location());

        switch (req.status()){
            case AVAILABLE -> office.setAvailable();
            case UNAVAILABLE ->  office.setUnavailable();
            case INACTIVE ->  office.setInactive();
        }
        return officeMapper.toResponse(officeRepo.save(office));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OfficeResponse> findAll() {
        return officeRepo.findAll().stream().map(officeMapper::toResponse).toList();
    }
}
