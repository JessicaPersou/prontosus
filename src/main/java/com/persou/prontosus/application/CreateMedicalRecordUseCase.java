package com.persou.prontosus.application;

import static com.persou.prontosus.config.MessagesErrorException.HEALTHCARE_PROFESSIONAL_NOT_FOUND;
import static com.persou.prontosus.config.MessagesErrorException.ENTITY_NOT_FOUND;

import com.persou.prontosus.config.exceptions.ResourceNotFoundException;
import com.persou.prontosus.domain.MedicalRecord;
import com.persou.prontosus.domain.Patient;
import com.persou.prontosus.domain.User;
import com.persou.prontosus.gateway.MedicalRecordRepository;
import com.persou.prontosus.gateway.PatientRepository;
import com.persou.prontosus.gateway.UserRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateMedicalRecordUseCase {

    private final MedicalRecordRepository medicalRecordRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    public MedicalRecord execute(String patientId, String healthcareProfessionalId, MedicalRecord medicalRecord) {
        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new ResourceNotFoundException(ENTITY_NOT_FOUND));

        User healthcareProfessional = userRepository.findById(healthcareProfessionalId)
            .orElseThrow(() -> new ResourceNotFoundException(HEALTHCARE_PROFESSIONAL_NOT_FOUND));

        MedicalRecord recordToSave = medicalRecord
            .withPatient(patient)
            .withHealthcareProfessional(healthcareProfessional);

        if (recordToSave.consultationDate() == null) {
            recordToSave = recordToSave.withConsultationDate(LocalDateTime.now());
        }

        return medicalRecordRepository.save(recordToSave);
    }
}
