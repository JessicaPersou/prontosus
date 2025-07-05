package com.persou.prontosus.application;

import com.persou.prontosus.config.exceptions.ResourceNotFoundException;
import com.persou.prontosus.config.mapper.PatientMapper;
import com.persou.prontosus.domain.Patient;
import com.persou.prontosus.gateway.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdatePatientUseCase {

    private final String PACTENTE_NOT_FOUND = "Paciente não encontrado";

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    public Patient execute(Long id, Patient updatedPatient) {
        Patient existingPatient = patientRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(PACTENTE_NOT_FOUND));

        var patientToSave = patientMapper.updateEntityFromDomain(updatedPatient, existingPatient);

        return patientRepository.save(patientToSave);
    }

}