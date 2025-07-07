package com.persou.prontosus.config.mapper;

import com.persou.prontosus.adapters.response.MedicalRecordResponse;
import com.persou.prontosus.domain.FileAttachment;
import com.persou.prontosus.domain.MedicalRecord;
import com.persou.prontosus.domain.valueobject.VitalSigns;
import com.persou.prontosus.gateway.database.jpa.FileAttachmentEntity;
import com.persou.prontosus.gateway.database.jpa.MedicalRecordEntity;
import com.persou.prontosus.gateway.database.jpa.VitalSignsEntity;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {PatientMapper.class, UserMapper.class,
    AppointmentMapper.class})
public interface MedicalRecordMapper {

    @Mapping(target = "id", source = "existingRecord.id")
    @Mapping(target = "patient", source = "existingRecord.patient")
    @Mapping(target = "healthcareProfessional", source = "existingRecord.healthcareProfessional")
    @Mapping(target = "appointment", source = "existingRecord.appointment")
    @Mapping(target = "consultationDate", source = "updatedRecord.consultationDate")
    @Mapping(target = "chiefComplaint", source = "updatedRecord.chiefComplaint")
    @Mapping(target = "historyOfPresentIllness", source = "updatedRecord.historyOfPresentIllness")
    @Mapping(target = "physicalExamination", source = "updatedRecord.physicalExamination")
    @Mapping(target = "vitalSigns", source = "updatedRecord.vitalSigns")
    @Mapping(target = "diagnosis", source = "updatedRecord.diagnosis")
    @Mapping(target = "treatment", source = "updatedRecord.treatment")
    @Mapping(target = "prescriptions", source = "updatedRecord.prescriptions")
    @Mapping(target = "observations", source = "updatedRecord.observations")
    @Mapping(target = "attachments", source = "existingRecord.attachments")
    @Mapping(target = "createdAt", source = "existingRecord.createdAt")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    MedicalRecord updateRecordFields(MedicalRecord existingRecord, MedicalRecord updatedRecord);

    @Mapping(target = "appointment", ignore = true)
    @Mapping(target = "vitalSigns", source = "vitalSigns")
    @Mapping(target = "attachments", source = "attachments")
    MedicalRecord toDomain(MedicalRecordEntity entity);

    @Mapping(target = "vitalSigns", source = "vitalSigns")
    @Mapping(target = "attachments", source = "attachments")
    MedicalRecordEntity toEntity(MedicalRecord domain);

    MedicalRecordResponse toResponse(MedicalRecord medicalRecord);

    default VitalSigns mapVitalSigns(VitalSignsEntity vitalSignsEntity) {
        if (vitalSignsEntity == null) {
            return null;
        }
        return VitalSigns.builder()
            .systolicPressure(vitalSignsEntity.getSystolicPressure())
            .diastolicPressure(vitalSignsEntity.getDiastolicPressure())
            .heartRate(vitalSignsEntity.getHeartRate())
            .temperature(vitalSignsEntity.getTemperature())
            .respiratoryRate(vitalSignsEntity.getRespiratoryRate())
            .weight(vitalSignsEntity.getWeight())
            .height(vitalSignsEntity.getHeight())
            .oxygenSaturation(vitalSignsEntity.getOxygenSaturation())
            .build();
    }

    default VitalSignsEntity mapVitalSigns(VitalSigns vitalSigns) {
        if (vitalSigns == null) {
            return null;
        }
        return new VitalSignsEntity(
            vitalSigns.systolicPressure(),
            vitalSigns.diastolicPressure(),
            vitalSigns.heartRate(),
            vitalSigns.temperature(),
            vitalSigns.respiratoryRate(),
            vitalSigns.weight(),
            vitalSigns.height(),
            vitalSigns.oxygenSaturation()
        );
    }

    default List<FileAttachment> mapAttachments(List<FileAttachmentEntity> attachmentEntities) {
        if (attachmentEntities == null) {
            return null;
        }
        return attachmentEntities.stream()
            .map(this::mapFileAttachment)
            .toList();
    }

    default List<FileAttachmentEntity> mapAttachmentEntities(List<FileAttachment> attachments) {
        if (attachments == null) {
            return null;
        }
        return attachments.stream()
            .map(this::mapFileAttachmentEntity)
            .toList();
    }

    default FileAttachment mapFileAttachment(FileAttachmentEntity entity) {
        if (entity == null) {
            return null;
        }
        return FileAttachment.builder()
            .id(entity.getId())
            .fileName(entity.getFileName())
            .filePath(entity.getFilePath())
            .contentType(entity.getContentType())
            .fileSize(entity.getFileSize())
            .fileType(entity.getFileType().name())
            .description(entity.getDescription())
            .uploadedAt(entity.getUploadedAt())
            .build();
    }

    default FileAttachmentEntity mapFileAttachmentEntity(FileAttachment attachment) {
        if (attachment == null) {
            return null;
        }
        return FileAttachmentEntity.builder()
            .id(attachment.id())
            .fileName(attachment.fileName())
            .filePath(attachment.filePath())
            .contentType(attachment.contentType())
            .fileSize(attachment.fileSize())
            .fileType(com.persou.prontosus.domain.enums.FileType.valueOf(attachment.fileType()))
            .description(attachment.description())
            .uploadedAt(attachment.uploadedAt())
            .build();
    }
}