package com.persou.prontosus.application;

import static com.persou.prontosus.config.MessagesErrorException.ENTITY_NOT_FOUND;
import static com.persou.prontosus.config.MessagesErrorException.HEALTHCARE_PROFESSIONAL_NOT_FOUND;

import com.persou.prontosus.config.exceptions.ResourceNotFoundException;
import com.persou.prontosus.domain.MedicalRecord;
import com.persou.prontosus.domain.Patient;
import com.persou.prontosus.domain.User;
import com.persou.prontosus.gateway.MedicalRecordRepository;
import com.persou.prontosus.gateway.PatientRepository;
import com.persou.prontosus.gateway.UserRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CreateMedicalRecordUseCase {

    private final MedicalRecordRepository medicalRecordRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    public MedicalRecord execute(String patientId, String healthcareProfessionalId, MedicalRecord medicalRecord) {
        log.info("Criando registro médico para paciente {} e profissional {}", patientId, healthcareProfessionalId);

        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> {
                log.error("Paciente não encontrado: {}", patientId);
                return new ResourceNotFoundException(ENTITY_NOT_FOUND + " - Paciente: " + patientId);
            });

        User healthcareProfessional = userRepository.findById(healthcareProfessionalId)
            .orElseThrow(() -> {
                log.error("Profissional não encontrado: {}", healthcareProfessionalId);
                return new ResourceNotFoundException(HEALTHCARE_PROFESSIONAL_NOT_FOUND + " - ID: " + healthcareProfessionalId);
            });

        log.info("Paciente encontrado: {} - {}", patient.id(), patient.fullName());
        log.info("Profissional encontrado: {} - {}", healthcareProfessional.id(), healthcareProfessional.fullName());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime consultationDate = medicalRecord.consultationDate() != null ?
            medicalRecord.consultationDate() : now;

        MedicalRecord recordToSave = MedicalRecord.builder()
            .patient(patient)
            .healthcareProfessional(healthcareProfessional)
            .consultationDate(consultationDate)
            .chiefComplaint(medicalRecord.chiefComplaint())
            .historyOfPresentIllness(medicalRecord.historyOfPresentIllness())
            .physicalExamination(medicalRecord.physicalExamination())
            .vitalSigns(medicalRecord.vitalSigns())
            .diagnosis(medicalRecord.diagnosis())
            .treatment(medicalRecord.treatment())
            .prescriptions(medicalRecord.prescriptions())
            .observations(medicalRecord.observations())
            .createdAt(now)
            .updatedAt(now)
            .build();

        log.info("Salvando registro médico...");
        MedicalRecord savedRecord = medicalRecordRepository.save(recordToSave);
        log.info("Registro médico salvo com sucesso: {}", savedRecord.id());

        return savedRecord;
    }
}