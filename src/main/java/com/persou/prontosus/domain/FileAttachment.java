package com.persou.prontosus.domain;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.With;

@With
@Builder(toBuilder = true)
public record FileAttachment(
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