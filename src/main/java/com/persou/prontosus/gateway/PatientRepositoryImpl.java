package com.persou.prontosus.gateway;

import com.persou.prontosus.config.exceptions.ResourceNotFoundException;
import com.persou.prontosus.config.mapper.PatientMapper;
import com.persou.prontosus.domain.Patient;
import com.persou.prontosus.gateway.database.jpa.repository.PatientJpaRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class PatientRepositoryImpl implements PatientRepository {

    public static final String PATIENT_ID_CANNOT_BE_NULL_OR_BLANK = "Patient ID cannot be null or blank";

    private final PatientJpaRepository patientJpaRepository;
    private final PatientMapper patientMapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<Patient> findByCpf(String cpf) {
        return patientJpaRepository.findByCpf(cpf)
            .map(patientMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Patient> findById(Long id) {
        if (id == null) {
            throw new ResourceNotFoundException(PATIENT_ID_CANNOT_BE_NULL_OR_BLANK);
        }
        var patientEntity = patientJpaRepository.findById(id);
        return patientEntity.map(patientMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Patient> findByFullNameContainingIgnoreCase(String name) {
        return patientJpaRepository.findByFullNameContainingIgnoreCase(name)
            .stream()
            .map(patientMapper::toDomain)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Patient> findAll() {
        return patientJpaRepository.findAll()
            .stream()
            .map(patientMapper::toDomain)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCpf(String cpf) {
        return patientJpaRepository.existsByCpf(cpf);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Patient> findByPhoneNumber(String phone) {
        return patientJpaRepository.findByPhoneNumber(phone)
            .stream()
            .map(patientMapper::toDomain)
            .toList();
    }

    @Override
    @Transactional
    public Patient save(Patient patient) {
        if (patient == null) {
            throw new IllegalArgumentException("Patient cannot be null");
        }
        var patientEntity = patientMapper.toEntity(patient);
        return patientMapper.toDomain(patientJpaRepository.save(patientEntity));
    }
}
