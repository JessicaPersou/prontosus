package com.persou.prontosus.adapters.request;

import com.persou.prontosus.domain.MedicalRecord;
import com.persou.prontosus.domain.User;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.With;

@With
@Builder(toBuilder = true)
public record FileAttachmentRequest(
    String id,
    MedicalRecord medicalRecord,
    String fileName,
    String filePath,
    String contentType,
    Long fileSize,
    String fileType,
    String description,
    LocalDateTime uploadedAt,
    User uploadedBy
) {
}