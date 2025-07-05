package com.persou.prontosus.gateway;

import com.persou.prontosus.config.mapper.FileAttachmentMapper;
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

    @Override
    public List<FileAttachment> findByMedicalRecordOrderByUploadedAtDesc(MedicalRecord medicalRecord) {
        return List.of();
    }

    @Override
    public List<FileAttachment> findByFileType(FileType fileType) {
        return List.of();
    }

    @Override
    public List<FileAttachment> findByPatientId(Long patientId) {
        return List.of();
    }

    @Override
    public List<FileAttachment> findByPatientIdAndFileType(Long patientId, FileType fileType) {
        return List.of();
    }

    @Override
    public FileAttachment save(FileAttachment fileAttachment) {
        var entity = fileAttachmentMapper.toEntity(fileAttachment);
        var savedEntity = fileAttachmentJpaRepository.save(entity);
        return fileAttachmentMapper.toDomain(savedEntity);
    }
}
