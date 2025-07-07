package com.persou.prontosus.application;

import static com.persou.prontosus.config.MessagesErrorException.ENTITY_NOT_FOUND;

import com.persou.prontosus.config.exceptions.ResourceNotFoundException;
import com.persou.prontosus.domain.MedicalRecord;
import com.persou.prontosus.gateway.database.jpa.repository.MedicalRecordJpaRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateMedicalRecordUseCase {

    private final MedicalRecordJpaRepository medicalRecordJpaRepository;

    @Transactional
    public MedicalRecord execute(String recordId, MedicalRecord updatedRecord) {
        try {
            log.info("Atualizando registro médico: {}", recordId);

            var existingEntity = medicalRecordJpaRepository.findById(recordId)
                .orElseThrow(() -> {
                    log.error("Registro médico não encontrado: {}", recordId);
                    return new ResourceNotFoundException(ENTITY_NOT_FOUND);
                });

            log.info("Registro médico encontrado: {}", existingEntity.getId());
            log.info("Data consulta atual: {}", existingEntity.getConsultationDate());

            if (existingEntity.getConsultationDate() == null) {
                log.warn("ConsultationDate está nulo, definindo como agora");
                existingEntity.setConsultationDate(LocalDateTime.now());
            }

            if (updatedRecord.chiefComplaint() != null) {
                existingEntity.setChiefComplaint(updatedRecord.chiefComplaint());
                log.debug("Atualizando chiefComplaint para: {}", updatedRecord.chiefComplaint());
            }
            if (updatedRecord.historyOfPresentIllness() != null) {
                existingEntity.setHistoryOfPresentIllness(updatedRecord.historyOfPresentIllness());
                log.debug("Atualizando historyOfPresentIllness");
            }
            if (updatedRecord.physicalExamination() != null) {
                existingEntity.setPhysicalExamination(updatedRecord.physicalExamination());
                log.debug("Atualizando physicalExamination");
            }
            if (updatedRecord.diagnosis() != null) {
                existingEntity.setDiagnosis(updatedRecord.diagnosis());
                log.debug("Atualizando diagnosis para: {}", updatedRecord.diagnosis());
            }
            if (updatedRecord.treatment() != null) {
                existingEntity.setTreatment(updatedRecord.treatment());
                log.debug("Atualizando treatment");
            }
            if (updatedRecord.prescriptions() != null) {
                existingEntity.setPrescriptions(updatedRecord.prescriptions());
                log.debug("Atualizando prescriptions");
            }
            if (updatedRecord.observations() != null) {
                existingEntity.setObservations(updatedRecord.observations());
                log.debug("Atualizando observations");
            }

            existingEntity.setUpdatedAt(LocalDateTime.now());

            if (existingEntity.getCreatedAt() == null) {
                existingEntity.setCreatedAt(LocalDateTime.now());
            }

            log.info("Salvando entity com consultationDate: {}", existingEntity.getConsultationDate());
            var savedEntity = medicalRecordJpaRepository.save(existingEntity);
            log.info("Registro médico atualizado com sucesso: {}", savedEntity.getId());

            return MedicalRecord.builder()
                .id(savedEntity.getId())
                .chiefComplaint(savedEntity.getChiefComplaint())
                .historyOfPresentIllness(savedEntity.getHistoryOfPresentIllness())
                .physicalExamination(savedEntity.getPhysicalExamination())
                .diagnosis(savedEntity.getDiagnosis())
                .treatment(savedEntity.getTreatment())
                .prescriptions(savedEntity.getPrescriptions())
                .observations(savedEntity.getObservations())
                .consultationDate(savedEntity.getConsultationDate())
                .createdAt(savedEntity.getCreatedAt())
                .updatedAt(savedEntity.getUpdatedAt())
                .build();

        } catch (Exception e) {
            log.error("Erro ao atualizar registro médico {}: {}", recordId, e.getMessage(), e);
            throw e;
        }
    }
}