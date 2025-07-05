package com.persou.prontosus.application;

import com.persou.prontosus.config.mapper.PatientMapper;
import com.persou.prontosus.domain.Patient;
import com.persou.prontosus.gateway.PatientRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FindPatientUseCase {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    public Optional<Patient> findById(Long id) {
        return patientRepository.findById(id);
    }

    public Optional<Patient> findByCpf(String cpf) {
        return patientRepository.findByCpf(cpf);
    }

    public List<Patient> findByName(String name) {
        return patientRepository.findByFullNameContainingIgnoreCase(name);
    }

    public List<Patient> findAll() {
        return patientRepository.findAll();
    }
}
