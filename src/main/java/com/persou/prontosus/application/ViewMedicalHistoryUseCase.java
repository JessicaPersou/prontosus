package com.persou.prontosus.application;

import com.persou.prontosus.domain.MedicalRecord;
import com.persou.prontosus.domain.User;
import com.persou.prontosus.gateway.MedicalRecordRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ViewMedicalHistoryUseCase {

    private final MedicalRecordRepository medicalRecordRepository;

    public List<MedicalRecord> getPatientHistory(String patientId) {
        return medicalRecordRepository.findByPatientIdOrderByConsultationDateDesc(patientId);
    }

    public List<MedicalRecord> getPatientHistoryByDateRange(String patientId, LocalDateTime startDate,
                                                            LocalDateTime endDate) {
        return medicalRecordRepository.findByPatientAndDateRange(patientId, startDate, endDate);
    }

    public List<MedicalRecord> getProfessionalRecords(Long professionalId) {
        var professional = User.builder().id(professionalId).build();
        return medicalRecordRepository.findByHealthcareProfessionalOrderByConsultationDateDesc(professional);
    }
}