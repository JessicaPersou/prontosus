package com.persou.prontosus.gateway;

import com.persou.prontosus.domain.MedicalRecord;
import com.persou.prontosus.domain.Patient;
import com.persou.prontosus.domain.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MedicalRecordRepository {
    List<MedicalRecord> findByPatientOrderByConsultationDateDesc(Patient patient);

    List<MedicalRecord> findByHealthcareProfessionalOrderByConsultationDateDesc(User healthcareProfessional);

    List<MedicalRecord> findByPatientIdOrderByConsultationDateDesc(Long patientId);

    List<MedicalRecord> findByConsultationDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<MedicalRecord> findByPatientAndDateRange(Long patientId, LocalDateTime startDate, LocalDateTime endDate);

    Optional<MedicalRecord> findById(Long id);

    MedicalRecord save(MedicalRecord medicalRecord);
}
