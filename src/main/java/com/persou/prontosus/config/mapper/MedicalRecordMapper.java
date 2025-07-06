package com.persou.prontosus.config.mapper;
import com.persou.prontosus.adapters.response.MedicalRecordResponse;
import com.persou.prontosus.domain.MedicalRecord;
import com.persou.prontosus.domain.valueobject.VitalSigns;
import com.persou.prontosus.gateway.database.jpa.MedicalRecordEntity;
import com.persou.prontosus.gateway.database.jpa.VitalSignsEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {PatientMapper.class, UserMapper.class, AppointmentMapper.class})
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

    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "vitalSigns", source = "vitalSigns")
    MedicalRecord toDomain(MedicalRecordEntity entity);

    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "vitalSigns", source = "vitalSigns")
    MedicalRecordEntity toEntity(MedicalRecord domain);

    MedicalRecordResponse toResponse(MedicalRecord medicalRecord);

    default VitalSigns map(VitalSignsEntity vitalSignsEntity) {
        if (vitalSignsEntity == null) {
            return null;
        }
        return new VitalSigns(
            vitalSignsEntity.getSystolicPressure(),
            vitalSignsEntity.getDiastolicPressure(),
            vitalSignsEntity.getHeartRate(),
            vitalSignsEntity.getTemperature(),
            vitalSignsEntity.getRespiratoryRate(),
            vitalSignsEntity.getWeight(),
            vitalSignsEntity.getHeight(),
            vitalSignsEntity.getOxygenSaturation()
        );
    }

    default VitalSignsEntity map(VitalSigns vitalSigns) {
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
}