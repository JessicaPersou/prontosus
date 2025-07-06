package com.persou.prontosus.gateway;

import com.persou.prontosus.config.mapper.FileAttachmentMapper;
import com.persou.prontosus.config.mapper.MedicalRecordMapper;
import com.persou.prontosus.domain.FileAttachment;
import com.persou.prontosus.domain.MedicalRecord;
import com.persou.prontosus.domain.enums.FileType;
import com.persou.prontosus.gateway.database.jpa.repository.FileAttachmentJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FileAttachmentRepositoryImpl implements FileAttachmentRepository {

    private final FileAttachmentJpaRepository fileAttachmentJpaRepository;
    private final FileAttachmentMapper fileAttachmentMapper;
    private final MedicalRecordMapper medicalRecordMapper;

    @Override
    public List<FileAttachment> findByMedicalRecordOrderByUploadedAtDesc(MedicalRecord medicalRecord) {
        var medicalRecordEntity = medicalRecordMapper.toEntity(medicalRecord);
        return fileAttachmentJpaRepository.findByMedicalRecordOrderByUploadedAtDesc(medicalRecordEntity)
            .stream()
            .map(fileAttachmentMapper::toDomain)
            .toList();
    }

    @Override
    public List<FileAttachment> findByFileType(FileType fileType) {
        return fileAttachmentJpaRepository.findByFileType(fileType)
            .stream()
            .map(fileAttachmentMapper::toDomain)
            .toList();
    }

    @Override
    public List<FileAttachment> findByPatientId(String patientId) {
        return fileAttachmentJpaRepository.findByPatientId(patientId)
            .stream()
            .map(fileAttachmentMapper::toDomain)
            .toList();
    }

    @Override
    public List<FileAttachment> findByPatientIdAndFileType(String patientId, FileType fileType) {
        return fileAttachmentJpaRepository.findByPatientIdAndFileType(patientId, fileType)
            .stream()
            .map(fileAttachmentMapper::toDomain)
            .toList();
    }

    @Override
    public FileAttachment save(FileAttachment fileAttachment) {
        var entity = fileAttachmentMapper.toEntity(fileAttachment);
        var savedEntity = fileAttachmentJpaRepository.save(entity);
        return fileAttachmentMapper.toDomain(savedEntity);
    }
}
