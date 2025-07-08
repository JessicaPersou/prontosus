package com.persou.prontosus.application;

import static com.persou.prontosus.config.MessagesErrorException.ENTITY_NOT_FOUND;

import com.persou.prontosus.config.exceptions.ResourceNotFoundException;
import com.persou.prontosus.domain.Patient;
import com.persou.prontosus.gateway.PatientRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdatePatientUseCase {

    private final PatientRepository patientRepository;

    public Patient execute(String id, Patient updatedPatient) {
        Patient existingPatient = patientRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(ENTITY_NOT_FOUND));

        Patient patientToSave = Patient.builder()
            .id(id)
            .cpf(updatedPatient.cpf())
            .fullName(updatedPatient.fullName())
            .birthDate(updatedPatient.birthDate())
            .gender(updatedPatient.gender())
            .phoneNumber(updatedPatient.phoneNumber())
            .email(updatedPatient.email())
            .address(updatedPatient.address())
            .emergencyContactName(updatedPatient.emergencyContactName())
            .emergencyContactPhone(updatedPatient.emergencyContactPhone())
            .knownAllergies(updatedPatient.knownAllergies())
            .currentMedications(updatedPatient.currentMedications())
            .chronicConditions(updatedPatient.chronicConditions())
            .medicalRecords(existingPatient.medicalRecords())
            .appointments(existingPatient.appointments())
            .createdAt(existingPatient.createdAt())
            .updatedAt(LocalDateTime.now())
            .build();

        return patientRepository.save(patientToSave);
    }
}