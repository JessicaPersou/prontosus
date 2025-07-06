package com.persou.prontosus.gateway;

import com.persou.prontosus.config.mapper.MedicalRecordMapper;
import com.persou.prontosus.domain.MedicalRecord;
import com.persou.prontosus.domain.Patient;
import com.persou.prontosus.domain.User;
import com.persou.prontosus.gateway.database.jpa.repository.MedicalRecordJpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MedicalRecordRepositoryImpl implements MedicalRecordRepository {

    private final MedicalRecordJpaRepository medicalRecordJpaRepository;
    private final MedicalRecordMapper medicalRecordMapper;

    @Override
    public List<MedicalRecord> findByPatientOrderByConsultationDateDesc(Patient patient) {
        return List.of();
    }

    @Override
    public List<MedicalRecord> findByHealthcareProfessionalOrderByConsultationDateDesc(User healthcareProfessional) {
        return List.of();
    }

    @Override
    public List<MedicalRecord> findByPatientIdOrderByConsultationDateDesc(String patientId) {
        return List.of();
    }

    @Override
    public List<MedicalRecord> findByConsultationDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return List.of();
    }

    @Override
    public List<MedicalRecord> findByPatientAndDateRange(String patientId, LocalDateTime startDate,
                                                         LocalDateTime endDate) {
        return List.of();
    }

    @Override
    public Optional<MedicalRecord> findById(String id) {
        return Optional.empty();
    }

    @Override
    public MedicalRecord save(MedicalRecord medicalRecord) {
        return null;
    }
}
