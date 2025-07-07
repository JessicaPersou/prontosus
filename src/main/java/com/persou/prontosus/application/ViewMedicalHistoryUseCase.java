package com.persou.prontosus.application;

import com.persou.prontosus.domain.MedicalRecord;
import com.persou.prontosus.gateway.MedicalRecordRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ViewMedicalHistoryUseCase {

    private final MedicalRecordRepository medicalRecordRepository;

    public List<MedicalRecord> getPatientHistory(String patientId) {
        log.info("Buscando histórico médico para paciente ID: {}", patientId);
        List<MedicalRecord> records = medicalRecordRepository.findByPatientIdOrderByConsultationDateDesc(patientId);
        log.info("Encontrados {} registros para o paciente {}", records.size(), patientId);

        if (records.isEmpty()) {
            log.warn("Nenhum registro médico encontrado para o paciente: {}", patientId);
        }

        return records;
    }

    public List<MedicalRecord> getPatientHistoryByDateRange(String patientId, LocalDateTime startDate,
                                                            LocalDateTime endDate) {
        log.info("Buscando histórico médico para paciente {} entre {} e {}", patientId, startDate, endDate);
        return medicalRecordRepository.findByPatientAndDateRange(patientId, startDate, endDate);
    }

    public List<MedicalRecord> getProfessionalRecords(String professionalId) {
        log.info("Buscando registros do profissional ID: {}", professionalId);
        List<MedicalRecord> records =
            medicalRecordRepository.findByHealthcareProfessionalIdOrderByConsultationDateDesc(professionalId);
        log.info("Encontrados {} registros para o profissional {}", records.size(), professionalId);
        return records;
    }
}