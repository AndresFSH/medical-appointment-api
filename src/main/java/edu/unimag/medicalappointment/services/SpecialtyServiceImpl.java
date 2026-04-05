package edu.unimag.medicalappointment.services;

import edu.unimag.medicalappointment.domain.entity.Specialty;
import edu.unimag.medicalappointment.dto.SpecialtyDTO;
import edu.unimag.medicalappointment.exception.ConflictException;
import edu.unimag.medicalappointment.mapper.SpecialtyMapper;
import edu.unimag.medicalappointment.repository.SpecialtyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecialtyServiceImpl implements SpecialtyService {

    private final SpecialtyRepository specialtyRepo;
    private final SpecialtyMapper specialtyMapper;

    @Override
    @Transactional
    public SpecialtyDTO.SpecialtyResponse createSpecialty(SpecialtyDTO.CreateSpecialtyRequest req) {
        if(specialtyRepo.existsByName(req.name())){
            throw new ConflictException("specialty with name " + req.name() + " already exists");
        }
        Specialty specialty = specialtyMapper.toEntity(req);
        return specialtyMapper.toResponse(specialtyRepo.save(specialty));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SpecialtyDTO.SpecialtyResponse> findAll() {
        return specialtyRepo.findAll().stream().map(specialtyMapper::toResponse).toList();
    }
}
