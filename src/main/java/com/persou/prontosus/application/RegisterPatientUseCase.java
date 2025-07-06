package com.persou.prontosus.application;

import static com.persou.prontosus.config.MessagesErrorException.DOCUMENT_ALREADY_EXISTS;

import com.persou.prontosus.config.exceptions.ResourceAlreadyExistsException;
import com.persou.prontosus.domain.Patient;
import com.persou.prontosus.gateway.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterPatientUseCase {

    private final PatientRepository patientRepository;

    public Patient execute(Patient patient) {
        validatePatient(patient);
        return patientRepository.save(patient);
    }

    private void validatePatient(Patient patient) {
        if (patientRepository.existsByCpf(patient.cpf())) {
            throw new ResourceAlreadyExistsException(DOCUMENT_ALREADY_EXISTS);
        }
    }
}
