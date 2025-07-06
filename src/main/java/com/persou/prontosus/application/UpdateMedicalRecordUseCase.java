package com.persou.prontosus.application;

import static com.persou.prontosus.config.MessagesErrorException.ENTITY_NOT_FOUND;

import com.persou.prontosus.config.exceptions.ResourceNotFoundException;
import com.persou.prontosus.config.mapper.MedicalRecordMapper;
import com.persou.prontosus.domain.MedicalRecord;
import com.persou.prontosus.gateway.MedicalRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateMedicalRecordUseCase {

    private final MedicalRecordRepository medicalRecordRepository;
    private final MedicalRecordMapper medicalRecordMapper;

    public MedicalRecord execute(String recordId, MedicalRecord updatedRecord) {
        MedicalRecord existingRecord = medicalRecordRepository.findById(recordId)
            .orElseThrow(() -> new ResourceNotFoundException(ENTITY_NOT_FOUND));

        var recordsToSave = medicalRecordMapper.updateRecordFields(existingRecord, updatedRecord);
        return medicalRecordRepository.save(recordsToSave);
    }

}
