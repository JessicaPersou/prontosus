package com.persou.prontosus.application;

import com.persou.prontosus.config.exceptions.ResourceNotFoundException;
import com.persou.prontosus.config.mapper.MedicalRecordMapper;
import com.persou.prontosus.domain.MedicalRecord;
import com.persou.prontosus.gateway.MedicalRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateMedicalRecordUseCase {

    public static final String MEDICAL_RECORD_NOT_FOUND = "Registro médico não encontrado";

    private final MedicalRecordRepository medicalRecordRepository;
    private final MedicalRecordMapper medicalRecordMapper;

    public MedicalRecord execute(Long recordId, MedicalRecord updatedRecord) {
        MedicalRecord existingRecord = medicalRecordRepository.findById(recordId)
            .orElseThrow(() -> new ResourceNotFoundException(MEDICAL_RECORD_NOT_FOUND));

        var recordsToSave = medicalRecordMapper.updateRecordFields(existingRecord, updatedRecord);
        return medicalRecordRepository.save(recordsToSave);
    }

}
