package com.persou.prontosus.application;

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

    public MedicalRecord execute(Long patientId, Long healthcareProfessionalId, MedicalRecord medicalRecord) {
        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado"));

        User healthcareProfessional = userRepository.findById(healthcareProfessionalId)
            .orElseThrow(() -> new IllegalArgumentException("Profissional de saúde não encontrado"));

        MedicalRecord recordToSave = medicalRecord
            .withPatient(patient)
            .withHealthcareProfessional(healthcareProfessional);

        if (recordToSave.consultationDate() == null) {
            recordToSave = recordToSave.withConsultationDate(LocalDateTime.now());
        }

        return medicalRecordRepository.save(recordToSave);
    }
}
