package com.persou.prontosus.gateway.database.jpa.repository;

import com.persou.prontosus.domain.enums.FileType;
import com.persou.prontosus.gateway.database.jpa.FileAttachmentEntity;
import com.persou.prontosus.gateway.database.jpa.MedicalRecordEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FileAttachmentJpaRepository extends JpaRepository<FileAttachmentEntity, Long> {
    List<FileAttachmentEntity> findByMedicalRecordOrderByUploadedAtDesc(MedicalRecordEntity medicalRecord);

    List<FileAttachmentEntity> findByFileType(FileType fileType);

    @Query("SELECT fa FROM FileAttachmentEntity fa WHERE fa.medicalRecord.patient.id = :patientId ORDER BY fa.uploadedAt DESC")
    List<FileAttachmentEntity> findByPatientId(@Param("patientId") Long patientId);

    @Query("SELECT fa FROM FileAttachmentEntity fa WHERE fa.medicalRecord.patient.id = :patientId AND fa.fileType = :fileType ORDER BY fa.uploadedAt DESC")
    List<FileAttachmentEntity> findByPatientIdAndFileType(@Param("patientId") Long patientId,
                                                          @Param("fileType") FileType fileType);
}
