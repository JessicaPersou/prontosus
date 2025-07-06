package com.persou.prontosus.gateway;

import com.persou.prontosus.config.mapper.MedicalRecordMapper;
import com.persou.prontosus.config.mapper.PatientMapper;
import com.persou.prontosus.config.mapper.UserMapper;
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
    private final PatientMapper patientMapper;
    private final UserMapper userMapper;

    @Override
    public List<MedicalRecord> findByPatientOrderByConsultationDateDesc(Patient patient) {
        var patientEntity = patientMapper.toEntity(patient);
        return medicalRecordJpaRepository.findByPatientOrderByConsultationDateDesc(patientEntity)
            .stream()
            .map(medicalRecordMapper::toDomain)
            .toList();
    }

    @Override
    public List<MedicalRecord> findByHealthcareProfessionalOrderByConsultationDateDesc(User healthcareProfessional) {
        var userEntity = userMapper.toEntity(healthcareProfessional);
        return medicalRecordJpaRepository.findByHealthcareProfessionalOrderByConsultationDateDesc(userEntity)
            .stream()
            .map(medicalRecordMapper::toDomain)
            .toList();
    }

    @Override
    public List<MedicalRecord> findByPatientIdOrderByConsultationDateDesc(String patientId) {
        return medicalRecordJpaRepository.findByPatientIdOrderByConsultationDateDesc(patientId)
            .stream()
            .map(medicalRecordMapper::toDomain)
            .toList();
    }

    @Override
    public List<MedicalRecord> findByConsultationDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return medicalRecordJpaRepository.findByConsultationDateBetween(startDate, endDate)
            .stream()
            .map(medicalRecordMapper::toDomain)
            .toList();
    }

    @Override
    public List<MedicalRecord> findByPatientAndDateRange(String patientId, LocalDateTime startDate, LocalDateTime endDate) {
        return medicalRecordJpaRepository.findByPatientAndDateRange(patientId, startDate, endDate)
            .stream()
            .map(medicalRecordMapper::toDomain)
            .toList();
    }

    @Override
    public Optional<MedicalRecord> findById(String id) {
        return medicalRecordJpaRepository.findById(id)
            .map(medicalRecordMapper::toDomain);
    }

    @Override
    public MedicalRecord save(MedicalRecord medicalRecord) {
        var entity = medicalRecordMapper.toEntity(medicalRecord);
        var savedEntity = medicalRecordJpaRepository.save(entity);
        return medicalRecordMapper.toDomain(savedEntity);
    }
}