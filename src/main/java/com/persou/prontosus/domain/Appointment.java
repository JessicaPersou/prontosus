package com.persou.prontosus.domain;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.With;

@With
@Builder(toBuilder = true)
public record Appointment(
    Long id,
    Patient patient,
    User healthcareProfessional,
    LocalDateTime scheduledDateTime,
    String status,
    String type,
    String reason,
    String notes,
    MedicalRecord medicalRecord,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
