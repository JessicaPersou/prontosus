package com.persou.prontosus.gateway;

import com.persou.prontosus.domain.FileAttachment;
import com.persou.prontosus.domain.MedicalRecord;
import com.persou.prontosus.domain.enums.FileType;
import java.util.List;

public interface FileAttachmentRepository {
    List<FileAttachment> findByMedicalRecordOrderByUploadedAtDesc(MedicalRecord medicalRecord);

    List<FileAttachment> findByFileType(FileType fileType);

    List<FileAttachment> findByPatientId(String patientId);

    List<FileAttachment> findByPatientIdAndFileType(String patientId, FileType fileType);

    FileAttachment save(FileAttachment fileAttachment);
}
