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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
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
            .map(entity -> {
                try {
                    return medicalRecordMapper.toDomain(entity);
                } catch (Exception e) {
                    log.error("Erro ao mapear MedicalRecordEntity para Domain: {}", e.getMessage(), e);
                    throw new RuntimeException("Erro no mapeamento do registro médico: " + entity.getId(), e);
                }
            })
            .toList();
    }

    @Override
    public List<MedicalRecord> findByHealthcareProfessionalOrderByConsultationDateDesc(User healthcareProfessional) {
        var userEntity = userMapper.toEntity(healthcareProfessional);
        return medicalRecordJpaRepository.findByHealthcareProfessionalOrderByConsultationDateDesc(userEntity)
            .stream()
            .map(entity -> {
                try {
                    return medicalRecordMapper.toDomain(entity);
                } catch (Exception e) {
                    log.error("Erro ao mapear MedicalRecordEntity para Domain: {}", e.getMessage(), e);
                    throw new RuntimeException("Erro no mapeamento do registro médico: " + entity.getId(), e);
                }
            })
            .toList();
    }

    @Override
    public List<MedicalRecord> findByPatientIdOrderByConsultationDateDesc(String patientId) {
        log.debug("Buscando registros médicos para paciente ID: {}", patientId);

        try {
            List<MedicalRecord> records =
                medicalRecordJpaRepository.findByPatientIdOrderByConsultationDateDesc(patientId)
                    .stream()
                    .map(entity -> {
                        log.debug("Mapeando entity com ID: {}", entity.getId());
                        try {
                            return medicalRecordMapper.toDomain(entity);
                        } catch (Exception e) {
                            log.error("Erro ao mapear MedicalRecordEntity {} para Domain: {}", entity.getId(),
                                e.getMessage());
                            log.error("Stack trace completo:", e);
                            throw new RuntimeException("Erro no mapeamento do registro médico: " + entity.getId(), e);
                        }
                    })
                    .toList();

            log.debug("Encontrados {} registros no repository para paciente {}", records.size(), patientId);
            return records;
        } catch (Exception e) {
            log.error("Erro geral ao buscar registros para paciente {}: {}", patientId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<MedicalRecord> findByHealthcareProfessionalIdOrderByConsultationDateDesc(String professionalId) {
        log.debug("Buscando registros médicos para profissional ID: {}", professionalId);
        List<MedicalRecord> records =
            medicalRecordJpaRepository.findByHealthcareProfessionalIdOrderByConsultationDateDesc(professionalId)
                .stream()
                .map(entity -> {
                    try {
                        return medicalRecordMapper.toDomain(entity);
                    } catch (Exception e) {
                        log.error("Erro ao mapear MedicalRecordEntity para Domain: {}", e.getMessage(), e);
                        throw new RuntimeException("Erro no mapeamento do registro médico: " + entity.getId(), e);
                    }
                })
                .toList();
        log.debug("Encontrados {} registros no repository para profissional {}", records.size(), professionalId);
        return records;
    }

    @Override
    public List<MedicalRecord> findByConsultationDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return medicalRecordJpaRepository.findByConsultationDateBetween(startDate, endDate)
            .stream()
            .map(medicalRecordMapper::toDomain)
            .toList();
    }

    @Override
    public List<MedicalRecord> findByPatientAndDateRange(String patientId, LocalDateTime startDate,
                                                         LocalDateTime endDate) {
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