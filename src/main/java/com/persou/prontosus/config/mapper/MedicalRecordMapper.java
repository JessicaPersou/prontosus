package com.persou.prontosus.config.mapper;
import com.persou.prontosus.domain.MedicalRecord;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MedicalRecordMapper {
    MedicalRecord updateRecordFields(MedicalRecord existingRecord, MedicalRecord updatedRecord);
}
