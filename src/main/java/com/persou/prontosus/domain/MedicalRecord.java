package com.persou.prontosus.domain;

import com.persou.prontosus.domain.valueobject.VitalSigns;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.With;

@With
@Builder(toBuilder = true)
public record MedicalRecord(
    String id,
    Patient patient,
    User healthcareProfessional,
    Appointment appointment,
    LocalDateTime consultationDate,
    String chiefComplaint,
    String historyOfPresentIllness,
    String physicalExamination,
    VitalSigns vitalSigns,
    String diagnosis,
    String treatment,
    String prescriptions,
    String observations,
    List<FileAttachment> attachments,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}